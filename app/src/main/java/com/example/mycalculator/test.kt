package com.example.mycalculator

import kotlinx.android.synthetic.main.activity_main.*

var nrBrackets = 3
val operators = listOf<String>("×","+","-","÷")
fun main() {
    //var text =  "(5×(7+1))×10" "(1+3)×10÷(5+(10-5))" "12+(5×10)" "(5×(7+1))×10" "12×5+10" "(1+3)×10÷(5+(10-5))"
    val text =  "(1+3)×10÷(5+(10-5))"

    val e = Expression(text)
    e.interpretValues()
    val values = e.values
    e.text=text
    println("e.values = $values")

    while(nrBrackets > 0){
        var tempText = e.text
        val from = e.text.indexOfLast {it.toString()=="("}
        val tempText2 = e.text.substring(from,tempText.length)
        val to = tempText2.indexOfFirst {it.toString()==")"} + from
        tempText = e.text.substring(from+1 until to)
        println("from $from to $to")

        println("tempText is $tempText")

        val subExpression = Expression(tempText)
        subExpression.interpretValues()
        subExpression.calculate()

        println("subValues = ${subExpression.values}")
        println("res is ${subExpression.res}")

        e.adjustText(subExpression.res,from,to)

        println("new Text ${e.text}")
        println("new e.values = ${e.values}")
        println("new priortey ${e.priorityRequired}")
        nrBrackets--
    }
    e.calculate()

    println(e.res)



    /*val values: MutableList<String> = mutableListOf()
    val i = text.iterator()
    var value = ""
    var c: String
    var prevOperator = false
    //checking if the first number starts with a minus or plus
    if (text[0].toString() in listOf("+", "-"))
        prevOperator = true
    //reading the text and separating numbers and operators, then putting them in #values to calculate later
    while (i.hasNext()) {
        c = i.next().toString()
        if (c in operators) {
            if (prevOperator) {
                value += c
            } else {
                values.add(value)
                values.add(c)
                value = ""
                prevOperator = true
            }
        } else if (c == "(") {
            value= ""
            values.add(c)
        } else if(c ==")"){
            values.add(value)
            value=")"
        }
        else {
            prevOperator = false
            value += c
        }
    }
    values.add(value)
    res = calculate(values)*/
}

private fun calculate(values : MutableList<String>): String{
    println("im working on $values")
    val tempValues=values
    var priorityRequired = 2
    var i = 0
    var changed = false
    var res = ""
    //checking for brackets
    if(nrBrackets > 0){
        var from = values.indexOfLast { it == "(" }
        var to = values.indexOfLast { it == ")" }
        --nrBrackets
        println("from $from to $to")
        if(from != to) {

            values.removeAt(to)
            values.removeAt(from)
            calculate(values.subList(from, to - 1))

            println(values)

            println("values after delete $values")
        }
        nrBrackets--
    }

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
    println("im done with $values")
    return values[0]
}