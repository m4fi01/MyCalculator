package com.example.mycalculator

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var lastDigit = false
    var lastComma = false
    var lastOperator = false
    var lastEqual = false
    var priorityRequired = 0
    var values : MutableList<String> = mutableListOf()
    val operators = listOf<String>("×","+","-","÷")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)


    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun digitClicked(view : View){
        calcText.setTextColor(getColor(R.color.lightGrey))
        lastDigit=true
        lastOperator=false
        if(lastEqual) {
            calcText.text = ""
            lastEqual = false
        }
        calcText.append((view as Button).text)

    }

    fun clearClicked(view : View) {
        calcText.text=""
        prevCalcText.text=""
        reset()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun operatorClicked(view : View){
        calcText.setTextColor(getColor(R.color.lightGrey))
        if(calcText.text.isEmpty() || lastOperator){
            if((view as Button) == btnPlus || view == btnMinus){
                calcText.append(view.text)
                lastOperator = false
            }
        }
        else if(lastDigit && !lastOperator){
            calcText.append((view as Button).text)
            if(view.text in listOf("×","÷")){
                priorityRequired++
            }
            lastOperator = true
            lastDigit = false
            lastEqual = false
        }
    }


    fun delClicked(view:View){
        if(!calcText.text.isEmpty()) {
            val text = calcText.text.toString()
            calcText.text = text.substring(0, text.length - 1)
        }
        else {
            Toast.makeText(this, "nothing to delete", Toast.LENGTH_SHORT).show()
            reset()
        }
    }

    fun commaClicked(view : View){
        if(lastDigit && !lastComma){
            calcText.append(".")
            reset()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun equalClicked(view : View){
        if(lastDigit) {
            prevCalcText.append(calcText.text)
            interpretValues()
            calculate()
            prevCalcText.append("=\n")
            //adding animation
            val anim = AnimationUtils.loadAnimation(this,R.anim.slide_up)
            calcText.startAnimation(anim)
            calcText.text = fmt(values[0].toDouble())
            calcText.setTextColor(getColor(R.color.lightGreen))

            reset()
            lastEqual = true
            lastDigit = true
        }
    }

    fun interpretValues(){
        var i = calcText.text.iterator()
        var value : String= ""
        var c: String = ""
        var prevOperator = false
        //checking if the first number starts with a minus or plus
        if(calcText.text[0].toString() in listOf("+","-"))
            prevOperator = true
        //reading the text and separating numbers and operators, then putting them in #values to calculate later
        while(i.hasNext()){
            c = i.next().toString()
            if(c in operators) {
                if(prevOperator){
                    value+=c
                } else {
                    values.add(value)
                    values.add(c)
                    value = ""
                    prevOperator = true
                }
            }
            else {
                prevOperator = false
                value+=c
            }
        }
        values.add(value)
    }

    fun calculate(){
        var res = values[0]
        var i = 0
        var changed = false
        while (i < values.size){
            //looking for the operator of higher value
            if(priorityRequired > 0){
                while (values[i] !in listOf("×","÷"))
                    i++
                priorityRequired--
                changed = true
            }

            val value = values[i]
            if(value in operators){
                val a = values[i-1].toDouble()
                val b = values[i+1].toDouble()
                when(value){
                    "+" -> res = (a+b).toString()
                    "-" -> res = (a-b).toString()
                    "×" -> res = (a*b).toString()
                    "÷" -> res = (a/b).toString()
                }
                values[i-1] = res
                values.removeAt(i)
                values.removeAt(i)
                i--
                //reset index to 0 if there was an calculation with priority
                if(changed){
                    changed = false
                    i = 0
                }
            }
            i++
        }
    }

    fun reset(){
        lastComma = false
        lastDigit = false
        lastOperator = false
        lastEqual = false
        priorityRequired = 0
        values.clear()
    }


    //to filter unnecessary after comma values in the result
    @RequiresApi(Build.VERSION_CODES.N)
    fun fmt(d: Double): String {
        if (d % 1.0 != 0.0000000)
            return String.format("%.10s", d);
        else
            return String.format("%.0f", d);

    }

    /*fun calculate(){
        var res = values[0]
        val i = values.listIterator()
        while (i.hasNext()){
            val value = i.next()
            if(value in operators){
                i.previous()
                val a = i.previous().toDouble()
                i.next()
                i.next()
                val b = i.next().toDouble()
                when(value){
                    "+" -> res = (a+b).toString()
                    "-" -> res = (a-b).toString()
                    "×" -> res = (a*b).toString()
                    "÷" -> res = (a/b).toString()
                }
                values.removeAt(0)
                values.removeAt(1)
                values.removeAt(2)

            }
        }
        calcText.text = res
    }*/



}