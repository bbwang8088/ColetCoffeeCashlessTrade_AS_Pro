/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class SerialPort {
	private static final String TAG = "SerialPort";
	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;

	static {
		System.loadLibrary("serial_port");
	}

	public SerialPort(File paramFile, int paramInt1, int paramInt2)
			throws SecurityException, IOException {
		if ((!paramFile.canRead()) || (!paramFile.canWrite()))
			try {
				Process localProcess = Runtime.getRuntime().exec(
						"/system/bin/su");
				String str = "chmod 666 " + paramFile.getAbsolutePath() + "\n"
						+ "exit\n";
				localProcess.getOutputStream().write(str.getBytes());
				if ((localProcess.waitFor() != 0) || (!paramFile.canRead())
						|| (!paramFile.canWrite()))
					throw new SecurityException();
			} catch (Exception localException) {
				localException.printStackTrace();
				throw new SecurityException();
			}
		this.mFd = open(paramFile.getAbsolutePath(), paramInt1, paramInt2);
		if (this.mFd == null) {
			Log.e("SerialPort", "native open returns null");
			throw new IOException();
		}
		this.mFileInputStream = new FileInputStream(this.mFd);
		this.mFileOutputStream = new FileOutputStream(this.mFd);
	}

	private static native FileDescriptor open(String paramString,
			int paramInt1, int paramInt2);

	public native void close();

	public InputStream getInputStream() {
		return this.mFileInputStream;
	}

	public OutputStream getOutputStream() {
		return this.mFileOutputStream;
	}
}
