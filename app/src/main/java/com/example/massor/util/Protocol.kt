package com.example.massor.util

import com.clj.fastble.utils.HexUtil
import kotlin.experimental.xor

/**
 * Created by ZY on 2018/6/20.
 */
object Protocol {
    /*val CMD_SET_FRAME    :Byte = 0x01
    val CMD_GET_FRAME    :Byte = 0x02
    val CMD_DEL_FRAME    :Byte = 0x03
    val CMD_SET_LOOP     :Byte = 0x04
    val CMD_GET_LOOP     :Byte = 0x05
    val CMD_RUN_PULSE    :Byte = 0x06
    val CMD_STOP_PULSE   :Byte = 0x07
    val CMD_PAUSE_PULSE  :Byte = 0x08
    val CMD_RESUME_PULSE :Byte = 0x09
    val CMD_SET_PULSEPWR :Byte = 0x0A
    val CMD_GET_PULSEPWR :Byte = 0x0B*/

    val CMD_SET_FRAME    :Int = 0
    val CMD_GET_FRAME    :Int = 1
    val CMD_DEL_FRAME    :Int = 2
    val CMD_SET_LOOP     :Int = 3
    val CMD_GET_LOOP     :Int = 4
    val CMD_RUN_PULSE    :Int = 5
    val CMD_STOP_PULSE   :Int = 6
    val CMD_PAUSE_PULSE  :Int = 7
    val CMD_RESUME_PULSE :Int = 8
    val CMD_SET_PULSEPWR :Int = 9
    val CMD_GET_PULSEPWR :Int = 10

    val CMD_Order : ByteArray = byteArrayOf(0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B)
    val CMD_Order_Receive : ByteArray = byteArrayOf(0x81.toByte(),0x82.toByte(),0x83.toByte(),0x84.toByte(),
            0x85.toByte(),0x86.toByte(),0x87.toByte(),0x88.toByte(),0x89.toByte(),0x8A.toByte(),0x8B.toByte())

    val CMD_Order_Receive_Fail : ByteArray = byteArrayOf(0xC1.toByte(),0xC2.toByte(),0xC3.toByte(),0xC4.toByte(),
            0xC5.toByte(),0xC6.toByte(),0xC7.toByte(),0xC8.toByte(),0xC9.toByte(),0xCA.toByte(),0xCB.toByte())
    var cmdIndex :Int = 0


    /*
     * 对发送数据进行组装
     */
    fun getSendCmdByteArr(order:Int, dataMap: HashMap<String, String>?):ByteArray{
        cmdIndex = 0
        //通信长度为64字节
        val byteArray = ByteArray(64)
        //通信头部 4字节
        setCmdSyn(byteArray)
        //MAC地址 6字节
        setCmdMAC(byteArray)
        //指令 1字节
        setCmdOrder(byteArray,order)
        //内容 52字节
        setCmdContent(byteArray,order,dataMap)
        //校验 1字节
        setCmdCheckData(byteArray)

        return  byteArray
    }

    private fun setCmdCheckData(array: ByteArray) {
        var check :Byte = array[0]
        for (i in 1..62){
            check = check xor array[i]
        }
        array[63] = check
    }

    private fun setCmdContent(array: ByteArray,order: Int,dataMap: HashMap<String, String>?) {
        when(order){
            CMD_SET_FRAME ->{
                //FrameParam
                //{25, 1, 0, 75, 20, 320, 80, 128, { 150, 2500,  75}, {  75,  75,  75,  75} }
                val frameIndex = dataMap!!["frameIndex"]!!.toInt().toByte()
                array[cmdIndex] = frameIndex
                cmdIndex++
                val framaParam = dataMap["frameParam"]
                val split = framaParam!!.split(" ")
                for (i in split.indices){
                    if (i == 0 || i == 1 || i==2 || i==4 || i==7){
                        array[cmdIndex] = split[i].toInt().toByte()
                        cmdIndex++
                    }else{
                        val int2ByteArr2 = StringUtil.Int2ByteArr2(split[i].toInt())
                        array[cmdIndex] = int2ByteArr2[0]
                        cmdIndex++
                        array[cmdIndex] = int2ByteArr2[1]
                        cmdIndex++
                    }
                }
            }
            CMD_GET_FRAME ->{
                val frameIndex = dataMap!!["frameIndex"]!!.toInt().toByte()
                array[cmdIndex] = frameIndex
                cmdIndex++
            }
            CMD_DEL_FRAME ->{
                val frameIndex = dataMap!!["frameIndex"]!!.toInt().toByte()
                array[cmdIndex] = frameIndex
                cmdIndex++
            }
            CMD_SET_LOOP ->{
                //LoopParam
                //12 0 12 8  因为最后一个8是2字节，所以存在高低位问题
                val loopParam = dataMap!!["loopParam"]
                val split = loopParam!!.split(" ")
                for (i in split.indices-1){
                    array[cmdIndex] = split[i].toInt().toByte()
                    cmdIndex++
                }
                val int2ByteArr2 = StringUtil.Int2ByteArr2(split.last().toInt())
                array[cmdIndex] = int2ByteArr2[0]
                cmdIndex++
                array[cmdIndex] = int2ByteArr2[1]
                cmdIndex++
            }
            CMD_GET_LOOP ->{

            }
            CMD_RUN_PULSE ->{
                println("Contete = "+HexUtil.formatHexString(array))
            }
            CMD_STOP_PULSE ->{

            }
            CMD_PAUSE_PULSE ->{

            }
            CMD_RESUME_PULSE ->{

            }
            CMD_SET_PULSEPWR ->{
                val pulsePWR = dataMap!!["pulsePWR"]!!.toInt().toByte()
                array[cmdIndex] = pulsePWR
                cmdIndex++
            }
            CMD_GET_PULSEPWR ->{

            }
        }
        if (cmdIndex<63){
            while (cmdIndex<63){
                array[cmdIndex] = 0x00
                cmdIndex++
            }
        }
    }

    private fun setCmdOrder(array: ByteArray,order: Int) {
        array[cmdIndex] = CMD_Order[order]
        cmdIndex++
    }

    private fun setCmdMAC(array: ByteArray) {
        array[cmdIndex] = 0x00
        cmdIndex++
        array[cmdIndex] = 0x00
        cmdIndex++
        array[cmdIndex] = 0x00
        cmdIndex++
        array[cmdIndex] = 0x00
        cmdIndex++
        array[cmdIndex] = 0x00
        cmdIndex++
        array[cmdIndex] = 0x00
        cmdIndex++
    }

    private fun setCmdSyn(array: ByteArray) {
        array[cmdIndex] = 0xEB.toByte()
        cmdIndex++
        array[cmdIndex] = 0x90.toByte()
        cmdIndex++
        array[cmdIndex] = 0xEB.toByte()
        cmdIndex++
        array[cmdIndex] = 0x90.toByte()
        cmdIndex++
    }

    /*
     *对接收到的数据进行解析
     */
    fun getReceiveCmdData(receiveByteArr:ByteArray):String{
        var content: String =""
        if (!checkReceive(receiveByteArr)){
            //数据校验没有通过
            println("数据校验没有通过")
        }else{
            content = getCmdOrderAndContent(receiveByteArr)
        }
        println("content="+content)
        return content
    }

    private fun getCmdOrderAndContent(receiveByteArr: ByteArray) :String{
        val Cmd = receiveByteArr[10]
        var order :Int = 11
        var content = StringBuilder()
        //成功
        for (i in CMD_Order_Receive.indices){
            if (Cmd == CMD_Order_Receive[i]){
                order = i
            }else if (Cmd == CMD_Order_Receive_Fail[i]){
                println("操作 "+i+" 执行失败！")
            }
        }
        println("order="+order)

        when(order){
            CMD_SET_FRAME ->{
                val frameIndex = receiveByteArr[11]
                content.append("设置序号：")
                content.append( frameIndex.toInt() and 0xFF)
            }
            CMD_GET_FRAME ->{
                val frameIndex = receiveByteArr[11]
                println("frameIndex="+frameIndex.toString())
                content.append("收到序号：")
                content.append(frameIndex.toInt() and 0xFF)
                content.append(" ")
                //FrameParam 25字节
                val param = phaseFrameParam(receiveByteArr)
                content.append("FrameParam:")
                content.append(param)
            }
            CMD_DEL_FRAME ->{
                val frameIndex = receiveByteArr[11]
                content.append("删除序号：")
                content.append(frameIndex.toInt() and 0xFF)
            }
            CMD_SET_LOOP ->{

            }
            CMD_GET_LOOP ->{
                //LoopParam
                val loopParam = phaseLoopParam(receiveByteArr)
                content.append("LoopParam：")
                content.append(loopParam)
            }
            CMD_RUN_PULSE ->{

            }
            CMD_STOP_PULSE ->{

            }
            CMD_PAUSE_PULSE ->{

            }
            CMD_RESUME_PULSE ->{

            }
            CMD_SET_PULSEPWR ->{
                val pulsePWR = receiveByteArr[11]
                content.append("设置强度为：")
                content.append(pulsePWR.toInt() and 0xFF)
            }
            CMD_GET_PULSEPWR ->{
                val pulsePWR = receiveByteArr[11]
                content.append("获取强度为：")
                content.append(pulsePWR.toInt() and 0xFF)
            }
        }
        return content.toString()
    }

    private fun phaseLoopParam(receiveByteArr: ByteArray):String {
        var index = 12
        val s = StringBuilder()
        for (i in 0..13){
            val toInt = receiveByteArr[index].toInt() and 0xFF
            index++
            s.append(toInt)
            s.append(" ")
        }
        val by = ByteArray(2)
        by[0] = receiveByteArr[index]
        index++
        by[1] = receiveByteArr[index]
        index++
        val arr22Int = StringUtil.ByteArr22Int(by)
        s.append(arr22Int)
        s.append(" ")

        return s.toString()
    }

    private fun phaseFrameParam(receiveByteArr: ByteArray):String {
        var index = 12
        val s = StringBuilder()
        for (i in 0..14){
            if (i == 0 || i == 1 || i==2 || i==4 || i==7){
                val toInt = receiveByteArr[index].toInt() and 0xFF
                index++
                s.append(toInt)
                s.append(" ")
            }else{
                val by = ByteArray(2)
                by[0] = receiveByteArr[index]
                index++
                by[1] = receiveByteArr[index]
                index++
                val arr22Int = StringUtil.ByteArr22Int(by)
                s.append(arr22Int)
                s.append(" ")
            }
        }
        return s.toString()
    }

    private fun checkReceive(receiveByteArr: ByteArray):Boolean {
        if (receiveByteArr[0] == 0xEB.toByte() && receiveByteArr[1] == 0x90.toByte() &&
                receiveByteArr[2] == 0xEB.toByte() && receiveByteArr[3] == 0x90.toByte() &&
                receiveByteArr.size == 64){
            var check :Byte = receiveByteArr[0]
            for (i in 1..62){
                check = check xor receiveByteArr[i]
            }
            if (check == receiveByteArr[63]){
                return true
            }
        }
        return false
    }

}