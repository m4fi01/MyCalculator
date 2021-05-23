package com.example.mycalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var lastDigit = false
    var lastComma = false
    var lastOperator = false
    var lastEqual = false
    var priorityRequired = 0
    var lastEqualPosition = 0
    var values : MutableList<String> = mutableListOf()
    val operators = listOf<String>("×","+","-","÷")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)


    }

    fun digitClicked(view : View){
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
        lastEqualPosition = 0
        reset()
    }

    fun operatorClicked(view : View){
        if(calcText.text.isEmpty()){
            if((view as Button) == btnPlus || view == btnMinus){
                calcText.append(view.text)
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
            calcText.append(",")

            reset()
        }
    }

    fun equalClicked(view : View){
        if(lastDigit) {
            interpretValues()
            calculate()
            lastEqualPosition = calcText.text.length +2
            calcText.append("=\n"+values[0])
            reset()
            lastEqual = true
            lastDigit = true
        }
    }

    fun interpretValues(){
        var i = calcText.text.toList().listIterator(lastEqualPosition)
        var value : String= ""
        var c: String = ""

        //reading the text and separating numbers and operators, then putting them in #values to calculate later
        while(i.hasNext()){
            c = i.next().toString()
            if(c !in operators)
                value+=c
            else {
                values.add(value)
                values.add(c)
                value = ""
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