package com.electricitybill.calculator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class BillAdapter(
    private val bills: List<BillRecord>,
    private val onCopy: (BillRecord) -> Unit
) : RecyclerView.Adapter<BillAdapter.VH>() {

    private val monthsShort = arrayOf(
        "Jan","Feb","Mar","Apr","May","Jun",
        "Jul","Aug","Sep","Oct","Nov","Dec"
    )

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvPeriod: TextView    = view.findViewById(R.id.tvPeriod)
        val tvMeta: TextView      = view.findViewById(R.id.tvMeta)
        val tvAmount: TextView    = view.findViewById(R.id.tvAmount)
        val btnCopy: MaterialButton = view.findViewById(R.id.btnCopyItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_bill, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val b = bills[position]
        holder.tvPeriod.text = "${monthsShort[b.m1]} – ${monthsShort[b.m2]}"
        holder.tvMeta.text   =
            "${b.last.toInt()} → ${b.curr.toInt()} kWh · ${b.units.toInt()} units\n" +
            "₹${b.rate}/unit · ${b.date}"
        holder.tvAmount.text = "₹${String.format("%,.2f", b.bill)}"
        holder.btnCopy.setOnClickListener { onCopy(b) }
    }

    override fun getItemCount() = bills.size
}
