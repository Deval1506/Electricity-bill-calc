package com.electricitybill.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.electricitybill.calculator.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCurrentRate()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        loadCurrentRate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadCurrentRate() {
        val rate = DataStore.getRate(requireContext())
        binding.etPrice.setText(rate.toString())
        binding.tvCurrentRate.setText("₹ $rate per unit")
    }

    private fun setupListeners() {
        binding.btnSaveRate.setOnClickListener {
            val p = binding.etPrice.text.toString().toDoubleOrNull()
            if (p == null || p <= 0) {
                Toast.makeText(requireContext(), "Enter a valid price", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            DataStore.saveRate(requireContext(), p)
            binding.tvCurrentRate.setText("₹ $p per unit")
            Toast.makeText(requireContext(), "Rate saved!", Toast.LENGTH_SHORT).show()
        }

        binding.btnClearHistory.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Clear History")
                .setMessage("Clear all saved bills? This cannot be undone.")
                .setPositiveButton("Clear") { _, _ ->
                    DataStore.clearHistory(requireContext())
                    Toast.makeText(requireContext(), "History cleared", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
