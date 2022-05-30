package com.example.graduationwork.activity.profile

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.graduationwork.R
import com.example.graduationwork.databinding.ActivitySettingsBinding
import com.example.graduationwork.activity.BaseActivity
import com.example.graduationwork.adapter.PhotoAdapter
import com.example.graduationwork.data.entity.Photo
import com.example.graduationwork.data.local.SharedPreference
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.InternalCoroutinesApi
import java.io.File

val photoAdapter by lazy { PhotoAdapter() }
@InternalCoroutinesApi
class SettingsActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingsBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar)

        binding.ivProfile.setColorFilter(
            ContextCompat.getColor(this, R.color.silver_chalice),
            PorterDuff.Mode.MULTIPLY
        )

        val name = intent.getStringExtra("name") ?: ""

        val avatar = intent.getStringExtra("avatar")

        binding.textName.setText(name)

        if (avatar != "") {
            binding.ivProfile.load(avatar)

        }

        binding.ivProfile.scaleType = ImageView.ScaleType.CENTER_CROP

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.layoutImage.setOnClickListener {
            photoBottomSheetDialog()
        }
        binding.buttonChange.setOnClickListener { patchProfile() }
    }

    private fun permissionPhotoDialog() {
        val dialog =
            MaterialAlertDialogBuilder(this, R.style.MyThemeOverlay_MaterialAlertDialog)

        dialog.setMessage(R.string.camera)

        dialog.setPositiveButton(R.string.permission_continue) { _, _ ->
            cameraPermissionResultLauncher.launch(android.Manifest.permission.CAMERA)
        }

        dialog.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun permissionGalleryDialog() {
        val dialog =
            MaterialAlertDialogBuilder(this, R.style.MyThemeOverlay_MaterialAlertDialog)

        dialog.setMessage(R.string.gallery)

        dialog.setPositiveButton(R.string.permission_continue) { _, _ ->
            galleryPermissionResultLauncher.launch(
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }

        dialog.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }

        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun photoBottomSheetDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet, null)
        dialog.setContentView(view)

        val rv = view.findViewById<RecyclerView>(R.id.recyclerViewBottomSheet)
        rv.apply {
            layoutManager = LinearLayoutManager(
                this@SettingsActivity,
                LinearLayoutManager.VERTICAL,
                false
            )

            val photo = listOf(
                Photo(1, getString(R.string.takePhoto)),
                Photo(2, getString(R.string.takeFromGallery))
            )

            adapter = photoAdapter
            photoAdapter.submitList(photo)
            photoAdapter.listener = {
                if (it.id == 1) {
                    when {
                        ContextCompat.checkSelfPermission(
                            this@SettingsActivity,
                            android.Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            takeImage()
                        }
                        shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)
                        -> {
                            permissionPhotoDialog()
                        }
                        else -> {
                            cameraPermissionResultLauncher.launch(
                                android.Manifest.permission.CAMERA
                            )
                        }
                    }
                    dialog.dismiss()
                }
                if (it.id == 2) {
                    when {
                        ContextCompat.checkSelfPermission(
                            this@SettingsActivity,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            selectImageFromGallery()
                        }
                        shouldShowRequestPermissionRationale(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        -> {
                            permissionGalleryDialog()
                        }
                        else -> {
                            galleryPermissionResultLauncher.launch(
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        }
                    }
                    dialog.dismiss()
                }
            }
            dialog.show()
        }
    }

    private var latestUri: Uri? = null

    private val cameraPermissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                takeImage()
            }
        }

    private val galleryPermissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                selectImageFromGallery()
            }
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri ->
            uri.let {
                latestUri = uri

                binding.ivProfile.setImageURI(it)
            }
        }

    private fun getTmpFileUri(): Uri {
        val context = this@SettingsActivity
        context.cacheDir.deleteRecursively()
        val file = File.createTempFile("tmp_image_file", ".jpg", context.cacheDir)
        return FileProvider.getUriForFile(
            context, context.packageName + ".fileProvider", file
        )
    }

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestUri?.let { uri ->
                    binding.ivProfile.setImageURI(uri)

                }
            }
        }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun patchProfile() = with(binding) {

        val name = textName.text.toString()

        showProgressDialog(resources.getString(R.string.wait))

        val sharedPreference = SharedPreference(this@SettingsActivity)
        val fileName = sharedPreference.getValueString("UID")
        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        if (latestUri != null) {
            storageReference.putFile(latestUri!!).addOnSuccessListener {

                updateName(name)

            }.addOnFailureListener {}
        } else updateName(name)
    }

    private fun updateName(name: String){

        val user = Firebase.auth.currentUser

        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    hideProgressDialog()
                    startActivity(
                        Intent(
                            this@SettingsActivity,
                            ProfileActivity::class.java
                        )
                    )
                    finish()
                } else {
                    hideProgressDialog()
                    showSnackBar(task.exception!!.message.toString(), true)

                }
            }
    }
}