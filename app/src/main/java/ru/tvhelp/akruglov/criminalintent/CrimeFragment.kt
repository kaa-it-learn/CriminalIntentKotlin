package ru.tvhelp.akruglov.criminalintent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_crime.*
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.customView
import org.jetbrains.anko.datePicker
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.verticalLayout
import java.io.File
import java.text.DateFormat
import java.text.FieldPosition
import java.text.SimpleDateFormat
import java.util.*

class CrimeFragment: Fragment() {

    companion object {
        private const val ARG_CRIME_ID = "crime_id"
        private const val ARG_CRIME_POSITION = "crime_position"

        private const val REQUEST_CONTACT = 0
        private const val REQUEST_PHOTO = 1

        fun newInstance(crimeId: UUID, crimePosition: Int): CrimeFragment {
            val args = Bundle()
            args.putSerializable(ARG_CRIME_ID, crimeId)
            args.putInt(ARG_CRIME_POSITION, crimePosition)

            val fragment = CrimeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var crime: Crime
    private lateinit var photoFile: File
    //private var crimePosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crimeId = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        val crimePosition = arguments?.getInt(ARG_CRIME_POSITION) as Int
        crime = CrimeLab.getInstance(activity as Context)[crimeId]!!
        photoFile = CrimeLab.getInstance(activity as Context).getPhotoFile(crime)
        setActivityResult(crimePosition)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_crime, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeTitle.setText(crime.title)
        crimeSolved.isChecked = crime.solved

        crimeTitle.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
        })

        updateDate()

        crimeSolved.setOnCheckedChangeListener { _, isChecked ->
            crime.solved = isChecked
        }

        crimeDate.setOnClickListener {

            val calendar = Calendar.getInstance()
            calendar.time = crime.date
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            alert() {
                title = getString(R.string.date_picker_title)
                customView {
                    verticalLayout {
                        val dateView = datePicker {
                            init(year, month, day, null)
                        }
                        positiveButton(android.R.string.ok) {
                            val date = GregorianCalendar(
                                    dateView.year,
                                    dateView.month,
                                    dateView.dayOfMonth).time
                            crime.date = date
                            updateDate()
                        }
                    }

                }

            }.show()
        }

        crimeReport.setOnClickListener {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_TEXT, getCrimeReport())
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            val ich = Intent.createChooser(i, getString(R.string.send_report))
            startActivity(ich)
        }

        val pickContact = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

        crimeSuspect.setOnClickListener {
            startActivityForResult(pickContact, REQUEST_CONTACT)
        }

        if (crime.suspect.isNotEmpty()) {
            crimeSuspect.text = crime.suspect
        }

        if (activity!!.packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            crimeSuspect.isEnabled = false
        }

        val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val canTakePhoto = captureImage.resolveActivity(activity!!.packageManager) != null
        crimeCamera.isEnabled = canTakePhoto

        crimeCamera.setOnClickListener {
            val uri = FileProvider.getUriForFile(activity!!,
                    "ru.tvhelp.akruglov.criminalintent.fileprovider",
                    photoFile)
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            val cameraActivities = activity!!.packageManager.queryIntentActivities(captureImage,
                    PackageManager.MATCH_DEFAULT_ONLY)
            for (activityInfo in cameraActivities) {
                activity!!.grantUriPermission(activityInfo.activityInfo.packageName,
                        uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }

            startActivityForResult(captureImage, REQUEST_PHOTO)
        }

        updatePhotoView()
    }


    override fun onPause() {
        super.onPause()

        CrimeLab.getInstance(activity!!).update(crime)
    }

    private fun updateDate() {
        crimeDate.text = crime.date.toString()
    }

    private fun updatePhotoView() {
        if (!photoFile.exists()) {
            crimePhoto.setImageDrawable(null)
        } else {
            val bitmap = getScaledBitmap(photoFile.path, activity!!)
            crimePhoto.imageBitmap = bitmap
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.solved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateFormat = "EEE, MMM dd"
        val dateString = SimpleDateFormat(dateFormat).format(crime.date)

        val suspect = if (crime.suspect.isEmpty()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        val report = getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)

        return report
    }

    private fun setActivityResult(crimePosition: Int) {
        val data = Intent()
        data.putExtra(CrimePagerActivity.EXTRA_CRIME_POSITION, crimePosition)
        activity?.setResult(Activity.RESULT_OK, data)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CONTACT && data != null) {
            val contactUri = data.data
            val queryFields = arrayOf<String>(ContactsContract.Contacts.DISPLAY_NAME)
            val c = activity!!.contentResolver.query(contactUri, queryFields, null, null, null)

            try {
                if (c.count == 0) {
                    return;
                }

                c.moveToFirst()
                crime.suspect = c.getString(0)
                crimeSuspect.text = crime.suspect
            } finally {
                c.close()
            }
        } else if (requestCode == REQUEST_PHOTO) {
            val uri = FileProvider.getUriForFile(activity!!,
                    "ru.tvhelp.akruglov.criminalintent.fileprovider",
                    photoFile)

            activity!!.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            updatePhotoView()
        }
    }
}