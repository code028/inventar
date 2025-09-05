package com.code028.inventoryapp.ui.inventory

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.code028.inventoryapp.R
import com.code028.inventoryapp.databinding.FragmentItemDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.code028.inventoryapp.data.model.Equipment
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class ItemDetailFragment : Fragment() {
    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val itemId = arguments?.getString("id") ?: return

        viewModel.getItem(itemId).addOnSuccessListener {
            val item = it.toObject(Equipment::class.java)

            binding.tvName.text = item?.name
            binding.tvCategory.text = "${item?.category}"
            binding.tvQuantity.text = "Количина: ${item?.quantity}"
            binding.tvLocation.text = item?.location ?: ""

            if (item?.status == true) {
                binding.tvStatus.text = "Активна ставка"
                binding.ivStatusIcon.setImageResource(R.drawable.ic_status)
            } else {
                binding.tvStatus.text = "Отписана ставка"
                binding.ivStatusIcon.setImageResource(R.drawable.ic_status2)
            }

            binding.tvDescription.text = item?.description
        }

        binding.btnDelete.setOnClickListener {
            viewModel.deleteItem(itemId).addOnSuccessListener {
                Toast.makeText(requireContext(), "Успешно обрисана ставка", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        binding.btnEdit.setOnClickListener {
            val action = ItemDetailFragmentDirections.actionItemDetailFragmentToEditItemFragment(itemId)
            findNavController().navigate(action)
        }

        binding.btnDownloadQr.setOnClickListener {
            val itemId = arguments?.getString("id") ?: return@setOnClickListener
            generateAndSaveQr(itemId)
        }

    }

    private fun generateAndSaveQr(itemId: String) {
        try {
            // 1. Create QR base
            val writer = com.google.zxing.qrcode.QRCodeWriter()
            val bitMatrix = writer.encode(itemId, com.google.zxing.BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height

            val qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    qrBitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }

            // 2. Dodaj labelu ispod QR-a
            val label = "ИНВЕНТАР"
            val textSize = 48f
            val paint = Paint().apply {
                color = Color.BLACK
                textAlign = Paint.Align.CENTER
                this.textSize = textSize
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
            }

            val textHeight = (paint.descent() - paint.ascent()).toInt()
            val combinedBitmap =
                Bitmap.createBitmap(width, height + textHeight + 20, Bitmap.Config.RGB_565)

            val canvas = Canvas(combinedBitmap)
            canvas.drawColor(Color.WHITE)

            // nacrtaj QR
            canvas.drawBitmap(qrBitmap, 0f, 0f, null)

            // nacrtaj tekst centrirano ispod
            canvas.drawText(
                label,
                (width / 2).toFloat(),
                (height + textHeight).toFloat(),
                paint
            )

            // 3. Save in Pictures/InventoryApp folder
            val filename = "QR_${itemId}.png"
            val picturesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val appDir = File(picturesDir, "InventoryApp")
            if (!appDir.exists()) appDir.mkdirs()

            val file = File(appDir, filename)
            val out = FileOutputStream(file)
            combinedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()

            // 4. Add to gallery
            MediaScannerConnection.scanFile(
                requireContext(),
                arrayOf(file.absolutePath),
                arrayOf("image/png"),
                null
            )

            Toast.makeText(requireContext(), "QR код је сачуван у галерији!", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Грешка при чувању QR кода!", Toast.LENGTH_LONG).show()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
