package expo.modules.uhfserialreader

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class UhfSerialReaderModule : Module() {
    val rfid = RFIDReaderManager()
    var isConnected = false

    override fun definition() =
        ModuleDefinition {
            Name("UhfSerialReader")

            Events("onRead")

            Function("connect") {
                rfid.connectReader(this@UhfReaderModule)
            }

            Function("disconnect") {
                rfid.disconnectReader()
            }
        }
}
