package com.example.mycalculator

class Expression( var text: String) {
    var values : MutableList<String> = mutableListOf()
    var priorityRequired = 0
    private val operators = listOf("×","+","-","÷")
    var res = ""


    fun interpretValues(){
        val values : MutableList<String> = mutableListOf()
        val i = text.iterator()
        var value = ""
        var c: String
        var prevOperator = false
        //checking if the first number starts with a minus or plus
        if(text[0].toString() in listOf("+","-"))
            prevOperator = true
        //reading the text and separating numbers and operators, then putting them in #values to calculate later
        while(i.hasNext()){
            c = i.next().toString()
            if(c in operators) {
                if(prevOperator){
                    value+=c
                }
                else {
                    values.add(value)
                    values.add(c)
                    value = ""
                    prevOperator = true
                }
            }
            else if (c == "(") {
                value= ""
                values.add(c)
            } else if(c ==")"){
                values.add(value)
                value=")"
            }
            else {
                prevOperator = false
                value+=c
            }
        }
        values.add(value)
        this.values = values
    }


    fun calculate(){
        var res = values[0]
        var i = 0
        var changed = false
        priorityCounter()
        while (i < values.size){
            //looking for the operator of higher value
            if(priorityRequired > 0){
                while (values[i] !in listOf("×", "÷"))
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
        this.res= values[0]
    }

    fun adjustText(toAdd : String,from : Int, to : Int){
        val temp = text.substring(from,to+1)
        text = text.replace(temp,toAdd)
        priorityCounter()
        this.interpretValues()
    }

    private  fun priorityCounter(){
        priorityRequired = 0
        priorityRequired += text.count { it.toString() == "(" }
        priorityRequired += text.count { it.toString() in listOf("×","÷") }
    }

}