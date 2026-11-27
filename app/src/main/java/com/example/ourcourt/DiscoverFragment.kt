package com.example.ourcourt

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ourcourt.data.model.TennisCourtPlace
import com.example.ourcourt.data.repository.PlacesRepository
import com.example.ourcourt.data.repository.PlacesRepositoryImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class DiscoverFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvCourtsSub: TextView
    private lateinit var tvLocationLabel: TextView
    private lateinit var layoutLocation: View
    private lateinit var layoutEmptyState: View
    private lateinit var etSearch: MaterialAutoCompleteTextView

    private lateinit var placesRepository: PlacesRepository
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var courtList = mutableListOf<TennisCourtPlace>()
    private lateinit var adapter: CourtAdapter

    // Default fallback user location (New York City)
    private var currentLocation: LatLng = LatLng(40.7829, -73.9654)

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            fetchDeviceLocationAndCourts()
        } else {
            // Permission denied: Use fallback location
            updateLocationHeader("New York")
            loadNearbyCourts(currentLocation)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_discover, container, false)
        recyclerView = view.findViewById(R.id.recycler_courts)
        tvCourtsSub = view.findViewById(R.id.tv_courts_sub)
        tvLocationLabel = view.findViewById(R.id.tv_location_label)
        layoutLocation = view.findViewById(R.id.layout_location)
        layoutEmptyState = view.findViewById(R.id.layout_empty_state)
        etSearch = view.findViewById(R.id.et_search)

        placesRepository = PlacesRepositoryImpl(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        var lastClickTime = 0L
        adapter = CourtAdapter(courtList) { selectedCourt ->
            val now = System.currentTimeMillis()
            if (now - lastClickTime > 600L) {
                lastClickTime = now
                val detailsFragment = CourtDetailsFragment.newInstance(selectedCourt)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, detailsFragment)
                    .addToBackStack("CourtDetails")
                    .commit()
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Setup US Restricted Autocomplete Suggestions Dropdown
        val autocompleteAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf()
        )
        etSearch.setAdapter(autocompleteAdapter)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s?.toString()?.trim() ?: ""
                if (input.length >= 2) {
                    fetchUSLocationPredictions(input, autocompleteAdapter)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        etSearch.setOnItemClickListener { _, _, position, _ ->
            val selectedSuggestion = autocompleteAdapter.getItem(position) ?: return@setOnItemClickListener
            performLocationSearch(selectedSuggestion)
            hideKeyboard(etSearch)
        }

        // Tapping location icon at top resets to current device GPS location
        layoutLocation.setOnClickListener {
            clearSavedSearchLocation()
            etSearch.setText("")
            Toast.makeText(requireContext(), "Reverting to current location", Toast.LENGTH_SHORT).show()
            checkLocationPermissionAndFetch()
        }

        // Setup Location Search Listener
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val query = etSearch.text.toString().trim()
                if (query.isNotBlank()) {
                    performLocationSearch(query)
                } else {
                    clearSavedSearchLocation()
                    checkLocationPermissionAndFetch()
                }
                hideKeyboard(etSearch)
                true
            } else {
                false
            }
        }

        updateState()

        // Check if there is a saved search location from previous session
        val savedSearch = getSavedSearchLocation()
        if (savedSearch != null) {
            etSearch.setText(savedSearch.query)
            updateLocationHeader(savedSearch.cityLabel)
            currentLocation = savedSearch.latLng
            loadNearbyCourts(savedSearch.latLng)
        } else {
            checkLocationPermissionAndFetch()
        }

        val btnFilter = view.findViewById<View>(R.id.btn_filter)
        btnFilter.setOnClickListener {
            val sheet = FilterBottomSheetDialogFragment()
            sheet.show(parentFragmentManager, "FilterBottomSheet")
        }

        return view
    }

    private fun fetchUSLocationPredictions(query: String, adapter: ArrayAdapter<String>) {
        val apiKey = BuildConfig.MAPS_API_KEY
        if (Places.isInitialized() && apiKey.isNotBlank() && apiKey != "YOUR_DEMO_KEY") {
            try {
                val placesClient = Places.createClient(requireContext().applicationContext)
                val request = FindAutocompletePredictionsRequest.builder()
                    .setCountries("US")
                    .setQuery(query)
                    .build()

                placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener { response ->
                        val predictions = response.autocompletePredictions.map { it.getFullText(null).toString() }
                        adapter.clear()
                        adapter.addAll(predictions)
                        adapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        fetchUSGeocoderSuggestions(query, adapter)
                    }
            } catch (_: Exception) {
                fetchUSGeocoderSuggestions(query, adapter)
            }
        } else {
            fetchUSGeocoderSuggestions(query, adapter)
        }
    }

    private fun fetchUSGeocoderSuggestions(query: String, adapter: ArrayAdapter<String>) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(requireContext(), Locale.US)
                val addresses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
                        geocoder.getFromLocationName(query, 5) { addrs ->
                            if (continuation.isActive) {
                                continuation.resumeWith(Result.success(addrs))
                            }
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocationName(query, 5)
                }

                val suggestions = addresses?.mapNotNull { addr ->
                    val city = addr.locality ?: addr.subAdminArea ?: addr.adminArea
                    val state = addr.adminArea
                    val country = addr.countryCode
                    if (country == "US" || country == "USA" || country == null) {
                        if (city != null && state != null) "$city, $state, USA" else addr.getAddressLine(0)
                    } else null
                }?.distinct() ?: emptyList()

                withContext(Dispatchers.Main) {
                    if (suggestions.isNotEmpty()) {
                        adapter.clear()
                        adapter.addAll(suggestions)
                        adapter.notifyDataSetChanged()
                    }
                }
            } catch (_: Exception) { }
        }
    }

    private fun performLocationSearch(query: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(requireContext(), Locale.US)
                val addresses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
                        geocoder.getFromLocationName(query, 1) { addrs ->
                            if (continuation.isActive) {
                                continuation.resumeWith(Result.success(addrs))
                            }
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocationName(query, 1)
                }

                if (!addresses.isNullOrEmpty()) {
                    val match = addresses[0]
                    val searchLatLng = LatLng(match.latitude, match.longitude)
                    
                    val cityLabel = match.locality
                        ?: match.subAdminArea
                        ?: match.adminArea
                        ?: match.postalCode
                        ?: query

                    saveSearchedLocation(query, cityLabel, searchLatLng)

                    withContext(Dispatchers.Main) {
                        currentLocation = searchLatLng
                        updateLocationHeader(cityLabel)
                        loadNearbyCourts(searchLatLng)
                        Toast.makeText(requireContext(), "Showing courts near $cityLabel", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Location not found for '$query'", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error searching location. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private data class SavedSearch(val query: String, val cityLabel: String, val latLng: LatLng)

    private fun saveSearchedLocation(query: String, cityLabel: String, latLng: LatLng) {
        val prefs = requireContext().getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("key_query", query)
            .putString("key_city_label", cityLabel)
            .putFloat("key_lat", latLng.latitude.toFloat())
            .putFloat("key_lng", latLng.longitude.toFloat())
            .apply()
    }

    private fun getSavedSearchLocation(): SavedSearch? {
        val prefs = requireContext().getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        val query = prefs.getString("key_query", null) ?: return null
        val cityLabel = prefs.getString("key_city_label", query) ?: query
        val lat = prefs.getFloat("key_lat", 0f).toDouble()
        val lng = prefs.getFloat("key_lng", 0f).toDouble()
        return SavedSearch(query, cityLabel, LatLng(lat, lng))
    }

    private fun clearSavedSearchLocation() {
        val prefs = requireContext().getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun checkLocationPermissionAndFetch() {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted || coarseLocationGranted) {
            fetchDeviceLocationAndCourts()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun fetchDeviceLocationAndCourts() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            updateLocationHeader("New York")
            loadNearbyCourts(currentLocation)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = LatLng(location.latitude, location.longitude)
                resolveCityName(currentLocation)
            } else {
                updateLocationHeader("New York")
            }
            loadNearbyCourts(currentLocation)
        }.addOnFailureListener {
            updateLocationHeader("New York")
            loadNearbyCourts(currentLocation)
        }
    }

    private fun resolveCityName(latLng: LatLng) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) { addresses ->
                        val city = addresses.firstOrNull()?.locality
                            ?: addresses.firstOrNull()?.subAdminArea
                            ?: addresses.firstOrNull()?.adminArea
                            ?: "Nearby"
                        lifecycleScope.launch(Dispatchers.Main) {
                            updateLocationHeader(city)
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    val city = addresses?.firstOrNull()?.locality
                        ?: addresses?.firstOrNull()?.subAdminArea
                        ?: addresses?.firstOrNull()?.adminArea
                        ?: "Nearby"
                    withContext(Dispatchers.Main) {
                        updateLocationHeader(city)
                    }
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    updateLocationHeader("Nearby")
                }
            }
        }
    }

    private fun updateLocationHeader(cityName: String) {
        tvLocationLabel.text = cityName
    }

    private fun loadNearbyCourts(location: LatLng) {
        lifecycleScope.launch {
            val result = placesRepository.getNearbyTennisCourts(location)
            result.onSuccess { courts ->
                courtList.clear()
                courtList.addAll(courts)
                adapter.notifyDataSetChanged()
                updateState()
            }.onFailure {
                updateState()
            }
        }
    }

    private fun updateState() {
        val count = courtList.size
        tvCourtsSub.text = "$count courts · Sorted by distance"
        if (courtList.isEmpty()) {
            recyclerView.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            layoutEmptyState.visibility = View.GONE
        }
    }
}

class CourtAdapter(
    private val courts: List<TennisCourtPlace>,
    private val onCourtClick: (TennisCourtPlace) -> Unit
) : RecyclerView.Adapter<CourtAdapter.CourtViewHolder>() {

    class CourtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_court_name)
        val tvRating: TextView = itemView.findViewById(R.id.tv_rating)
        val tvDetails: TextView = itemView.findViewById(R.id.tv_court_details)
        val tvSurface: TextView = itemView.findViewById(R.id.tv_surface_tag)
        val tvOpenStatus: TextView = itemView.findViewById(R.id.tv_open_status)
        val ivThumb: ImageView = itemView.findViewById(R.id.iv_court_thumb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourtViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_court_card, parent, false)
        return CourtViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourtViewHolder, position: Int) {
        val court = courts[position]
        holder.tvName.text = court.name
        holder.tvRating.text = court.formattedRating

        val addressOrDetails = court.shortFormattedAddress ?: court.formattedAddress ?: "Tennis Court"
        holder.tvDetails.text = "${court.formattedDistance} · $addressOrDetails"

        val surfaceText = court.primaryType?.replace("_", " ")?.replaceFirstChar { it.uppercase() } ?: "Tennis Court"
        holder.tvSurface.text = surfaceText

        val openText = when (court.isOpen) {
            true -> "Open"
            false -> "Closed"
            null -> court.businessStatus ?: "Available"
        }
        holder.tvOpenStatus.text = openText
        holder.ivThumb.setImageResource(court.fallbackDrawableRes)

        holder.itemView.setOnClickListener {
            onCourtClick(court)
        }
    }

    override fun getItemCount(): Int = courts.size
}
