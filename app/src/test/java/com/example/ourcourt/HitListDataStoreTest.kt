package com.example.ourcourt

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HitListDataStoreTest {

    @Before
    fun setUp() {
        HitListDataStore.clearAll()
    }

    @Test
    fun testInitialStateIsEmpty() {
        val visits = HitListDataStore.getVisitedCourts()
        assertEquals(0, visits.size)
        assertEquals(0, HitListDataStore.getUniqueCount())
    }

    @Test
    fun testRecordVisitsAndDeduplication() {
        HitListDataStore.recordVisit("c1", "Court One", "San Francisco, CA", "Nov 01, 2026", 1000L)
        HitListDataStore.recordVisit("c2", "Court Two", "San Francisco, CA", "Nov 02, 2026", 2000L)

        var list = HitListDataStore.getVisitedCourts()
        assertEquals(2, list.size)
        assertEquals("c2", list[0].courtId)

        // Revisit c1 with a newer date/timestamp
        HitListDataStore.recordVisit("c1", "Court One", "San Francisco, CA", "Nov 18, 2026", 3000L)

        list = HitListDataStore.getVisitedCourts()
        assertEquals(2, list.size)
        assertEquals("c1", list[0].courtId)
        assertEquals("Nov 18, 2026", list[0].lastVisitedDate)
    }

    @Test
    fun testVisibilityToggle() {
        HitListDataStore.recordVisit("c1", "Court One", "San Francisco, CA", "Nov 01, 2026", 1000L)
        HitListDataStore.updateDefaultVisibility(false)
        assertFalse(HitListDataStore.isPublicDefault)
        val visits = HitListDataStore.getVisitedCourts()
        assertTrue(visits.all { !it.isPublic })

        HitListDataStore.updateDefaultVisibility(true)
        assertTrue(HitListDataStore.isPublicDefault)
        assertTrue(HitListDataStore.getVisitedCourts().all { it.isPublic })
    }
}
