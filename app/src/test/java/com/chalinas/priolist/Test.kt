package com.chalinas.priolist

import com.chalinas.priolist.models.Task
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

class Test {

    @Test
    fun someTest() {
        val task = Task("id", "title", "description", Date(), categoryId = 1, 1, 1, 1L)
        assertEquals("title", task.title)
        assertEquals("description", task.description)
    }
}
