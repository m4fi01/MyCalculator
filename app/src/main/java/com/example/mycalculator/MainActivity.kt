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
    var nrBrackets = 0
    var rightBrackets= 0
    var leftBrackets = 0
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
            lastOperator = true
            lastDigit = false
            lastEqual = false
        }
    }


    fun delClicked(view:View){
        if(!calcText.text.isEmpty()) {
            val text = calcText.text.toString()
            calcText.text = text.substring(0, text.length - 1)
            val c = text[text.length-1].toString()
            if(c=="(")
                rightBrackets--
            else if (c in operators)
                lastOperator= true
            else{
                lastDigit=true
                lastOperator=false
            }

        }
        else {
            Toast.makeText(this, "nothing to delete", Toast.LENGTH_SHORT).show()
            reset()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun bracketsClicked(view : View){
        calcText.setTextColor(getColor(R.color.lightGrey))
        if((view as Button).text == "(") {
            calcText.append("(")
            rightBrackets++
        } else{
            if(leftBrackets < rightBrackets){
                calcText.append(")")
                leftBrackets++
            }
            else{
                Toast.makeText(this,"please use an opening bracket first",Toast.LENGTH_SHORT).show()
            }
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
            //checking if the brackets are closed
            if(rightBrackets != leftBrackets){
                Toast.makeText(this,"please close all the brackets first",Toast.LENGTH_SHORT).show()
                return
            }
            nrBrackets = rightBrackets
            prevCalcText.append(calcText.text)
            val e = Expression(calcText.text.toString())
            e.interpretValues()

            //solving brackets
            while(nrBrackets > 0){
                var tempText = e.text
                val from = e.text.indexOfLast {it.toString()=="("}
                tempText = e.text.substring(from,tempText.length)
                val to = tempText.indexOfFirst {it.toString()==")"} + from
                tempText = e.text.substring(from+1 until to)

                val subExpression = Expression(tempText)
                subExpression.interpretValues()
                subExpression.calculate()

                e.adjustText(subExpression.res,from,to)

                nrBrackets--
            }

            e.calculate()
            prevCalcText.append("=\n")
            //adding little animation
            val anim = AnimationUtils.loadAnimation(this,R.anim.slide_up)
            calcText.startAnimation(anim)

            calcText.text = fmt(e.values[0].toDouble())
            calcText.setTextColor(getColor(R.color.lightGreen))

            reset()
            lastEqual = true
            lastDigit = true
        }
    }

    private fun reset(){
        lastComma = false
        lastDigit = false
        lastOperator = false
        lastEqual = false
        nrBrackets = 0
        leftBrackets = 0
        rightBrackets = 0
    }


    //to filter unnecessary after comma values in the result
    @RequiresApi(Build.VERSION_CODES.N)
    fun fmt(d: Double): String {
        return if (d % 1.0 != 0.0000000)
            String.format("%.10s", d)
        else
            String.format("%.0f", d)

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