package com.electricitybill.calculator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.electricitybill.calculator.databinding.FragmentCalculatorBinding
import java.text.SimpleDateFormat
import java.util.*

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    private val monthsFull = arrayOf(
        "January","February","March","April","May","June",
        "July","August","September","October","November","December"
    )
    private val monthsShort = arrayOf(
        "Jan","Feb","Mar","Apr","May","Jun",
        "Jul","Aug","Sep","Oct","Nov","Dec"
    )

    private var rate = 7.5
    private var history = mutableListOf<BillRecord>()
    // Guard to stop spinner listeners firing during programmatic setup
    private var isInitializing = false

    // ── Lifecycle ──────────────────────────────────────────────────────────

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Populate spinners
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, monthsFull)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerM1.adapter = adapter
        binding.spinnerM2.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, monthsFull
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        loadData()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ── Data ───────────────────────────────────────────────────────────────

    private fun loadData() {
        rate    = DataStore.getRate(requireContext())
        history = DataStore.getHistory(requireContext())
        applyNext()
    }

    // ── Listeners ──────────────────────────────────────────────────────────

    private fun setupListeners() {
        binding.spinnerM1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                if (!isInitializing) {
                    binding.spinnerM2.setSelection((pos + 1) % 12)
                }
                doCalc()
            }
            override fun onNothingSelected(p: AdapterView<*>?) = Unit
        }

        binding.etLastReading.addTextChangedListener(SimpleWatcher { doCalc() })
        binding.etCurrReading.addTextChangedListener(SimpleWatcher { doCalc() })

        binding.btnCopy.setOnClickListener { copyCalc() }
        binding.btnSave.setOnClickListener { saveBill() }
    }

    // ── Logic (mirrors PWA exactly) ────────────────────────────────────────

    /** Returns the suggested (m1, m2) pair for the next billing period. */
    private fun nextPair(): Pair<Int, Int> {
        if (history.isEmpty()) {
            val m = Calendar.getInstance().get(Calendar.MONTH)
            val b = (m / 2) * 2
            return Pair(b, (b + 1) % 12)
        }
        return Pair((history[0].m2 + 1) % 12, (history[0].m2 + 2) % 12)
    }

    private fun applyNext() {
        val (a, b) = nextPair()
        isInitializing = true
        binding.spinnerM1.setSelection(a)
        binding.spinnerM2.setSelection(b)
        isInitializing = false
        if (history.isNotEmpty()) {
            binding.etLastReading.setText(history[0].curr.toInt().toString())
        }
        doCalc()
    }

    private fun doCalc() {
        val l = binding.etLastReading.text.toString().toDoubleOrNull() ?: 0.0
        val c = binding.etCurrReading.text.toString().toDoubleOrNull() ?: 0.0
        val u = if (c > l) c - l else 0.0
        binding.etUnits.setText(if (u > 0) u.toInt().toString() else "")
        binding.etRateDisplay.setText("₹ $rate")
        binding.tvBillAmount.text = if (u > 0)
            "₹ ${String.format("%,.2f", u * rate)}"
        else
            "₹ —"
    }

    private fun saveBill() {
        val l = binding.etLastReading.text.toString().toDoubleOrNull()
        val c = binding.etCurrReading.text.toString().toDoubleOrNull()
        if (l == null || c == null || c <= l) {
            showToast("⚠ Current reading must be greater than last")
            return
        }
        val u    = c - l
        val bill = String.format("%.2f", u * rate).toDouble()
        val m1   = binding.spinnerM1.selectedItemPosition
        val m2   = binding.spinnerM2.selectedItemPosition
        val date = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date())

        history.add(0, BillRecord(
            id = System.currentTimeMillis(),
            m1 = m1, m2 = m2,
            last = l, curr = c,
            units = u, bill = bill, rate = rate,
            date = date
        ))
        DataStore.saveHistory(requireContext(), history)

        binding.etCurrReading.setText("")
        applyNext()
        showToast("Bill saved!")
    }

    private fun copyCalc() {
        val l = binding.etLastReading.text.toString().toDoubleOrNull()
        val c = binding.etCurrReading.text.toString().toDoubleOrNull()
        if (l == null || c == null || c <= l) {
            showToast("Enter valid readings first")
            return
        }
        val u    = c - l
        val bill = u * rate
        val m1   = binding.spinnerM1.selectedItemPosition
        val m2   = binding.spinnerM2.selectedItemPosition
        val text = buildBillText(
            BillRecord(0, m1, m2, l, c, u, bill, rate, "")
        )
        copyToClipboard(text)
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    fun buildBillText(b: BillRecord): String =
        "Billing Period: ${monthsShort[b.m1]} – ${monthsShort[b.m2]}\n" +
        "Last reading: ${b.last.toInt()}\n" +
        "Current reading: ${b.curr.toInt()}\n" +
        "Unit: ${b.units.toInt()}\n" +
        "₹/Unit: ${b.rate}\n" +
        "Bill: ₹${String.format("%,.2f", b.bill)}"

    fun copyToClipboard(text: String) {
        val cb = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cb.setPrimaryClip(ClipData.newPlainText("Bill", text))
        showToast("Copied to clipboard")
    }

    private fun showToast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}
