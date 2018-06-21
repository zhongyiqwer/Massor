package com.example.massor.dao

/**
 * Created by ZY on 2018/6/4.
 */
object Lvs {
    /**
     * 0xEB 0x90 0xEB 0x90 地址（MAC 6bytes） 指令（1 byte） 内容（52 bytes）校验（1 byte，Xor）
     * WaveTPoint:1 1 1 2 1
     * LoopParam:1 1 12 1 1
     * FrameParam:1 4 4
     * BwShape:2
     * BwShapeStpe:1
     */

    //data class WaveTPoint(val amp :Int,val lasting :Int,val dutyOn :Int,val dutyOff :Int,val phaseAltTime :Int)

    data class LoopParam(val Max:Int,val index:Int,val frames:ArrayList<FrameParam>,
                         val stopSecond:Int)

    //data class FrameParam(val baseName:Int,val wTP_Large:ArrayList<WaveTPoint>,val wTP_Small:ArrayList<WaveTPoint>)
    data class FrameParam(val baseT:Int,val baseNo:Int,val phaModeHope:Int,val phaseAltTime: Int,val dutyOn: Int,
                          val dutyOff_Mid: Int,val dutyOff_Top: Int,val ampMid:Int,val LT:ArrayList<Int>)

    //data class BwShape(val m_bwShape:Int)

   // data class BwShapeStpe(val m_bwShapeStpe:Int)
}