package expo.modules.uhfserialreader

import android.util.Log
import com.uhf.scanable.UHFData
import java.util.Timer
import java.util.TimerTask

class RFIDReaderManager {
    var uhfSerialReaderModule: UhfSerialReaderModule? = null
    var antCheckList = [0, 0, 0, 0]

    private var timer: Timer? = null

    fun connectReader(module: UhfSerialReaderModule) {
        uhfSerialReaderModule = module

        try {
            val devport = "/dev/ttyS1"
            val result = UHFData.UHFGetData.openUhf(devport, 57600, 1, null)

            if (result == 0) {
                Log.i("UhfSerialReader", "Connected to Scanner Successfully!")
                uhfSerialReaderModule?.isConnected = true

                startScanningHandler()
            } else {
                Log.e("UhfSerialReader", "Failed To Connect Scanner!")
                Log.e("UhfSerialReader", "Error Code: $result")
                uhfSerialReaderModule?.isConnected = false
            }
        } catch (e: Exception) {
            Log.e("UhfSerialReader", "Error Connecting to Scanner!")
            e.printStackTrace()
            uhfSerialReaderModule?.isConnected = false
        }

        return uhfSerialReaderModule?.isConnected ?: false
    }

    fun startScanningHandler() {
        try {
            if (timer == null) {
                selectedEd = 255
                tid_flag = 0
                antCheckList[0] = 1
                antCheckList[1] = 1
                antCheckList[2] = 1
                antCheckList[3] = 1
                antIndex = 0
                timer = Timer()

                timer?.schedule(
                    object : TimerTask() {
                        override fun run() {
                            if (Scanflag) return
                            Scanflag = true
                            when (antIndex) {
                                0 ->
                                    if (antCheckList[0] == 1) {
                                        inventory6C(selectedEd, tid_flag, 0x80)
                                    }
                                1 ->
                                    if (antCheckList[1] == 1) {
                                        inventory6C(selectedEd, tid_flag, 0x81)
                                    }
                                2 ->
                                    if (antCheckList[2] == 1) {
                                        inventory6C(selectedEd, tid_flag, 0x82)
                                    }
                                3 ->
                                    if (antCheckList[3] == 1) {
                                        inventory6C(selectedEd, tid_flag, 0x83)
                                    }
                            }
                            antIndex++
                            if (antIndex > 3) antIndex = 0
                            Scanflag = false
                        }
                    },
                    0,
                    SCAN_INTERVAL.toLong(),
                )
            } else {
                timer?.cancel()
                timer = null
            }
        } catch (e: Exception) {
            Log.e("UhfSerialReader", "Error Starting Scanning Handler!")
            e.printStackTrace()
        }
    }

    fun inventory6C(
        session: Int,
        tid_flag: Int,
        ant: Int,
    ) {
        try {
            val label = UHfData.UHfGetData.Scan6C(session, tid_flag, ant)
            val scannedNum: Int

            if (label == null) {
                Log.i("UhfSerialReader", "No Tags Found!")
                return
            }

            scannedNum = label.size
            for (i in 0 until scannedNum) {
                val epc = label[i].substring(0, label[i].length - 2)
                val rssi = label[i].substring(label[i].length - 2)

                Log.i("UhfSerialReader", "EPC: $epc, RSSI: $rssi")

                if (epc.isEmpty()) return

                module?.sendEvent("onRead", mapOf("epc" to epc, "rssi" to rssi))
            }
        } catch (e: Exception) {
            Log.e("UhfSerialReader", "Error Scanning: ${e.message}")
            e.printStackTrace()
        }
    }

    fun disconnectReader() {
        try {
            UHFData.UHFGetData.closeUhf()
            uhfSerialReaderModule?.isConnected = false
            Log.i("UhfSerialReader", "Disconnected from Scanner Successfully!")
        } catch (e: Exception) {
            Log.e("UhfSerialReader", "Error Disconnecting from Scanner!")
            e.printStackTrace()
        }
    }
}
