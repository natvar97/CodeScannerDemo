package com.indialone.codescannerdemo

import android.app.AlertDialog
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.*
import com.indialone.codescannerdemo.databinding.ActivityMainBinding
import org.w3c.dom.Document
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringReader
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var codeScanner: CodeScanner
    private var result = ""

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

//                parseXml(it.text)


                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Scan Result")
                alertDialog.setMessage(result)
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

    fun parseXml(xmlString: String) {
        try {
            val xmlFactory = XmlPullParserFactory.newInstance()
            val parser = xmlFactory.newPullParser()

            xmlFactory.isNamespaceAware = true

            parser.setInput(StringReader(xmlString))
            var eventType = parser.eventType

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d("TAG", "Start document");
                } else if (eventType == XmlPullParser.START_TAG) {
                    Log.d("TAG", "Start tag " + parser.name);
                } else if (eventType == XmlPullParser.END_TAG) {
                    Log.d("TAG", "End tag " + parser.name);
                } else if (eventType == XmlPullParser.TEXT) {
                    Log.d("TAG", "Text " + parser.text); // here you get the text from xml
                    result = "\n" + result + parser.text
                }
                eventType = parser.next();
            }
            Log.d("TAG", "End document");
        } catch (e: XmlPullParserException) {
            e.printStackTrace();
        } catch (e: IOException) {
            e.printStackTrace();
        }
    }

    fun xMLfromString(xml: String?): Document? {
        var doc: Document? = null
        val dbf: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
        doc = try {
            val db: DocumentBuilder = dbf.newDocumentBuilder()
            val `is` = InputSource()
            `is`.characterStream = StringReader(xml)
            db.parse(`is`)
        } catch (e: ParserConfigurationException) {
            System.out.println("XML parse error: " + e.message)
            return null
        } catch (e: SAXException) {
            System.out.println("Wrong XML file structure: " + e.message)
            return null
        } catch (e: IOException) {
            println("I/O exeption: " + e.message)
            return null
        }
        return doc
    }


}