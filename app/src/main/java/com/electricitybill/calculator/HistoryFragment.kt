package com.electricitybill.calculator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.electricitybill.calculator.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val monthsShort = arrayOf(
        "Jan","Feb","Mar","Apr","May","Jun",
        "Jul","Aug","Sep","Oct","Nov","Dec"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refresh()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun refresh() {
        val history = DataStore.getHistory(requireContext())

        if (history.isEmpty()) {
            binding.recyclerHistory.visibility = View.GONE
            binding.emptyView.visibility       = View.VISIBLE
        } else {
            binding.emptyView.visibility       = View.GONE
            binding.recyclerHistory.visibility = View.VISIBLE
            binding.recyclerHistory.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerHistory.adapter = BillAdapter(history) { bill -> copyBill(bill) }
        }
    }

    private fun copyBill(b: BillRecord) {
        val text =
            "Billing Period: ${monthsShort[b.m1]} – ${monthsShort[b.m2]}\n" +
            "Last reading: ${b.last.toInt()}\n" +
            "Current reading: ${b.curr.toInt()}\n" +
            "Unit: ${b.units.toInt()}\n" +
            "₹/Unit: ${b.rate}\n" +
            "Bill: ₹${String.format("%,.2f", b.bill)}"
        val cb = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cb.setPrimaryClip(ClipData.newPlainText("Bill", text))
        Toast.makeText(requireContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}
