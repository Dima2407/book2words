package org.book2words.services

import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import org.book2words.core.Logger

public abstract class B2WHandler<T> : Handler(), B2WResponseWaiter<T> {
    companion object {
        public val RESULT_OK: Int = 100

        public val RESULT_FAILED: Int = -100

        public fun send<T>(receiver: Messenger, success: Boolean, data: T?): Boolean {
            if (success) {
                return sendSuccess(receiver, data)
            }
            return sendError(receiver, data)
        }

        public fun sendSuccess<T>(receiver: Messenger, data: T?): Boolean {
            val message = Message.obtain()
            message.arg1 = RESULT_OK
            message.obj = data
            try {
                receiver.send(message)
                return true
            } catch (e: RemoteException) {
                Logger.error(e)
            }
            return false
        }

        public fun sendError<T>(receiver: Messenger, data: T?): Boolean {
            val message = Message.obtain()
            message.arg1 = RESULT_FAILED
            message.obj = data
            try {
                receiver.send(message)
                return true
            } catch (e: RemoteException) {
                Logger.error(e)
            }
            return false
        }
    }

    override fun handleMessage(msg: Message) {
        super<Handler>.handleMessage(msg)
        onResult(msg.arg1 == RESULT_OK, msg.obj as T);
    }
}
