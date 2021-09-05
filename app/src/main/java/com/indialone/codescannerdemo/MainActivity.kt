package com.indialone.codescannerdemo

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.indialone.codescannerdemo.databinding.ActivityMainBinding
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        codeScanner = CodeScanner(this@MainActivity, mBinding.scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = true

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {

                val xmlFactory = XmlPullParserFactory.newInstance()
                val parser = xmlFactory.newPullParser()

                xmlFactory.isNamespaceAware = true

                parser.setInput(StringReader(it.text))
                var eventType = parser.eventType

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if(eventType == XmlPullParser.START_DOCUMENT) {
                        Log.d("TAG","Start document");
                    } else if(eventType == XmlPullParser.START_TAG) {
                        Log.d("TAG","Start tag "+parser.getName());
                    } else if(eventType == XmlPullParser.END_TAG) {
                        Log.d("TAG","End tag "+parser.getName());
                    } else if(eventType == XmlPullParser.TEXT) {
                        Log.d("TAG","Text "+parser.getText()); // here you get the text from xml
                    }
                    eventType = parser.next();
                }
                Log.d("TAG","End document");

                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Scan Result")
                alertDialog.setMessage(it.text)
                alertDialog.setNegativeButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                alertDialog.show()

            }
        }

        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        mBinding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }


    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()
    }


}