package dev.chalinas.priolist.data

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dev.chalinas.priolist.databinding.FragmentReminderListBinding
import dev.chalinas.priolist.viewmodel.ReminderViewModel
import dev.chalinas.priolist.adapter.ReminderListAdapter

data class Reminder(val id: Int, val taskId: Int, val remindAt: String, val isNotified: Boolean)

class ReminderListFragment : Fragment() {

    private var _binding: FragmentReminderListBinding? = null
    private val binding get() = _binding!!
    private val reminderViewModel: ReminderViewModel by viewModels()
    private lateinit var reminderListAdapter: ReminderListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReminderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setting up the RecyclerView
        reminderListAdapter = ReminderListAdapter()
        binding.reminderRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reminderListAdapter
        }

        // Observing data from the ViewModel
        reminderViewModel.allReminders.observe(viewLifecycleOwner, Observer { reminders ->
            reminders?.let {
                reminderListAdapter.submitList(it)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
