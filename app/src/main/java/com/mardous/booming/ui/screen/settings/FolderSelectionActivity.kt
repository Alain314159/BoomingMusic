/*
 * Copyright (c) 2024 Christians Martínez Alvarado
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mardous.booming.ui.screen.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mardous.booming.R
import com.mardous.booming.data.scanner.FolderSelectionManager
import com.mardous.booming.data.scanner.PermissionManager
import com.mardous.booming.data.scanner.ScanFolder
import com.mardous.booming.databinding.ActivityFolderSelectionBinding
import com.mardous.booming.databinding.ItemFolderBinding
import com.mardous.booming.extensions.showToast
import com.mardous.booming.ui.component.base.AbsThemeActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Activity para seleccionar carpetas de música para escanear.
 * Soporta carpetas predeterminadas y carpetas SAF seleccionadas por el usuario.
 */
class FolderSelectionActivity : AbsThemeActivity() {

    private lateinit var binding: ActivityFolderSelectionBinding

    private val folderManager: FolderSelectionManager by inject()
    private val permissionManager: PermissionManager by inject()

    private val foldersFlow = MutableStateFlow<List<ScanFolder>>(emptyList())

    private val defaultFoldersAdapter = FolderAdapter()
    private val customFoldersAdapter = FolderAdapter(isCustom = true)

    private val safLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let { treeUri ->
            // Persistir permiso
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(treeUri, takeFlags)

            // Guardar carpeta
            folderManager.addUserSelectedFolder(treeUri)

            // Recargar lista
            loadFolders()

            showToast(getString(R.string.folder_added))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerViews()
        setupButtons()
        observeFolders()
        loadFolders()
        updatePermissionStatus()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerViews() {
        binding.defaultFoldersRecyclerView.apply {
            adapter = defaultFoldersAdapter
            layoutManager = LinearLayoutManager(this@FolderSelectionActivity)
            isNestedScrollingEnabled = false
        }

        binding.customFoldersRecyclerView.apply {
            adapter = customFoldersAdapter
            layoutManager = LinearLayoutManager(this@FolderSelectionActivity)
            isNestedScrollingEnabled = false
        }
    }

    private fun setupButtons() {
        binding.addFolderButton.setOnClickListener {
            safLauncher.launch(null)
        }

        binding.scanNowButton.setOnClickListener {
            showScanProgressDialog()
        }

        binding.grantPermissionButton.setOnClickListener {
            safLauncher.launch(null)
        }
    }

    private fun observeFolders() {
        lifecycleScope.launch {
            foldersFlow.collectLatest { folders ->
                val defaultFolders = folders.filter { it.isDefault }
                val customFolders = folders.filter { !it.isDefault }

                defaultFoldersAdapter.submitList(defaultFolders)
                customFoldersAdapter.submitList(customFolders)

                binding.emptyText.visibility = if (folders.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun loadFolders() {
        val folders = folderManager.getScanFolders()
        foldersFlow.value = folders
    }

    private fun updatePermissionStatus() {
        val accessLevel = permissionManager.getAccessLevel()

        binding.permissionStatusText.text = when (accessLevel) {
            PermissionManager.StorageAccessLevel.NONE ->
                getString(R.string.permission_status_denied)
            PermissionManager.StorageAccessLevel.LEGACY ->
                getString(R.string.permission_status_granted) + " - " + getString(R.string.default_folders_only)
            PermissionManager.StorageAccessLevel.SAF ->
                getString(R.string.permission_status_granted)
            PermissionManager.StorageAccessLevel.MANAGE_ALL ->
                getString(R.string.permission_status_granted) + " - Full Access"
        }

        binding.grantPermissionButton.visibility = if (accessLevel == PermissionManager.StorageAccessLevel.NONE) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun showScanProgressDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_scan_progress, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.show()

        // Aquí se integraría con MediaScannerManager para mostrar progreso real
        dialogView.findViewById<View>(R.id.doneButton).setOnClickListener {
            dialog.dismiss()
        }
    }

    inner class FolderAdapter(
        private val isCustom: Boolean = false
    ) : RecyclerView.Adapter<FolderViewHolder>() {

        private var folders: List<ScanFolder> = emptyList()

        fun submitList(newFolders: List<ScanFolder>) {
            folders = newFolders
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
            val binding = ItemFolderBinding.inflate(
                layoutInflater, parent, false
            )
            return FolderViewHolder(binding)
        }

        override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
            holder.bind(folders[position])
        }

        override fun getItemCount(): Int = folders.size
    }

    inner class FolderViewHolder(
        private val binding: ItemFolderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(folder: ScanFolder) {
            binding.folderName.text = folder.displayName
            binding.folderPath.text = folder.path

            binding.folderEnabledSwitch.isChecked = folder.isEnabled
            binding.folderEnabledSwitch.setOnCheckedChangeListener { _, isChecked ->
                folderManager.setFolderEnabled(folder.uri ?: folder.path, isChecked)
                showToast(
                    if (isChecked) getString(R.string.folder_enabled)
                    else getString(R.string.folder_disabled)
                )
            }

            binding.removeButton.visibility = if (isCustom) View.VISIBLE else View.GONE
            binding.removeButton.setOnClickListener {
                AlertDialog.Builder(this@FolderSelectionActivity)
                    .setTitle(R.string.remove_folder)
                    .setMessage(getString(R.string.remove_folder_confirm, folder.displayName))
                    .setPositiveButton(R.string.remove) { _, _ ->
                        folder.uri?.let { uriString ->
                            val uri = Uri.parse(uriString)
                            folderManager.removeUserSelectedFolder(uri)
                            loadFolders()
                            showToast(getString(R.string.folder_removed))
                        }
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }
        }
    }
}
