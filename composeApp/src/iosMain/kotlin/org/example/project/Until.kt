package org.example.project

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CC_SHA256
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH
import platform.UIKit.UIDevice
import kotlin.experimental.and



actual object Until {
    @OptIn(ExperimentalForeignApi::class)
    actual fun sha256(input: String): String {
        val data = input.encodeToByteArray()
        val hash = UByteArray(CC_SHA256_DIGEST_LENGTH)

        data.usePinned { pinned ->
            hash.usePinned { digestPinned ->
                CC_SHA256(
                    pinned.addressOf(0),
                    data.size.toUInt(),
                    digestPinned.addressOf(0)
                )
            }
        }

        return hash.joinToString("") { byte ->
            (byte.toInt() and 0xFF)
                .toString(16)
                .padStart(2, '0')   // 🔥 заменяет format
        }
    }
    actual fun getDeviceId(): String {
        return UIDevice.currentDevice.identifierForVendor?.UUIDString ?: "unknown"
    }
}

actual object ToastManager {
    actual fun show(message: String) {
        // Здесь можно потом реализовать свой кастомный "тоаст" для iOS
        println("Toast(iOS): $message")
    }
}