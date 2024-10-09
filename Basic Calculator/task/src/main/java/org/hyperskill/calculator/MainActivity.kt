package org.hyperskill.calculator

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import org.hyperskill.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firstTerm: String
    private lateinit var etDisplay: EditText
    private lateinit var operator: String
    private var afterOp = 0
    private var secondTerm = ""
    private var term = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.displayEditText.inputType = InputType.TYPE_NULL
        // set OnClickerListener for every button in the layout
        binding.gridLayout.children.forEach { btn ->
            if (btn is Button) {
                btn.setOnClickListener(this@MainActivity)
            }
        }
    }

    override fun onClick(v: View) {
        etDisplay = binding.displayEditText
        if (v is Button) {
            when(v.id) {
                R.id.button0 -> button0(etDisplay)
                R.id.dotButton -> dotButton(etDisplay)
                R.id.clearButton -> { etDisplay.text.clear(); etDisplay.setHint("0") }
                R.id.addButton -> getTerms(etDisplay.text.toString(), "+")
                R.id.subtractButton -> getTerms(etDisplay.text.toString(), "-")
                R.id.multiplyButton -> getTerms(etDisplay.text.toString(), "*")
                R.id.divideButton -> getTerms(etDisplay.text.toString(), "/")
                R.id.equalButton ->
                    if (etDisplay.text.isEmpty()) {
                        if (secondTerm.isNotEmpty()) {
                            operator.operate(firstTerm, secondTerm)
                        } else {
                            operator.operate(firstTerm, firstTerm)
                        }
                    } else {
                        operator.operate(firstTerm, etDisplay.text.toString())
                    }

                else -> numButton(etDisplay, v.text.toString())
            }
        }
    }

    private fun String.operate(a: String, b: String) {
        val aDbl = a.toDouble()
        val bDbl = b.toDouble()
        val result = when(this) {
                "+" -> aDbl.plus(bDbl)
                "-" -> aDbl.minus(bDbl)
                "*" -> aDbl.times(bDbl)
                "/" -> aDbl.div(bDbl)
                else -> 0
            }
        val test = regexTest(result.toString())
        etDisplay.hint = if (test) result.toInt().toString() else result.toString()
        etDisplay.text.clear()
        afterOp = 1
        firstTerm = result.toString()
    }

    private fun getTerms(a: String, op: String) {
        val hint = etDisplay.hint.toString()
        var num = a
        if (num == "") num = hint
        etDisplay.hint = if (regexTest(num)) num.toDouble().toInt().toString() else num
        when {
            op == "-" && a == "" && afterOp == 1 -> {
                term = num; etDisplay.text.clear(); afterOp = 0; operator = op }
            op == "-" && a == "" && afterOp == 0 -> { etDisplay.text.append(op) }
            else -> { term = num; etDisplay.text.clear(); afterOp = 0; operator = op}
        }
        if (term != "") firstTerm = term
        secondTerm = ""
    }

    private fun button0(v: EditText) {
        with(v.text) { if (isEmpty()) {
            append("0")
        } else { if (first() == '0' && length == 1) {
            return
        } else if (first() == '-')  {
            if ((length == 2 && get(1) != '0') || length == 1) {
                append("0")
            } else if (length >= 3 && get(2) == '.') {
                append("0")
            } else {
                return
            }
        } else {
            append("0")
        }
            }
        }
    }

    private fun dotButton(v: EditText) {
        if ('.' !in v.text) {
            if (v.text.isEmpty() || ('-' in v.text && v.text.length == 1)) {
                v.text.append("0.")
            } else {
                v.text.append(".")
            }
        } else return
    }

    private fun numButton(v: EditText, btnText: String) {
        v.text.apply {
            if (isNotEmpty() && first() == '0' && length == 1) clear()
            if (isNotEmpty() && v.text.toString() == "-0" && length == 2) delete(1, 2)
            append(btnText)
        }
        secondTerm = btnText
    }

    private fun regexTest(string: String): Boolean {
        return Regex("-?\\d+.0").matches(string)
    }
}