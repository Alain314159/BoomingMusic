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

package com.mardous.booming.ui.component.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import com.google.android.material.button.MaterialButton
import com.mardous.booming.R

class PermissionView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private var mNumberView: TextView? = null
    private var mTitleView: TextView? = null
    private var mDescView: TextView? = null
    private var mButton: MaterialButton? = null

    private var mTitle: String? = null
    private var mDescription: String? = null
    private var mButtonText: String? = null
    private var mButtonIcon: Drawable? = null
    private var mNumber: Int = 0

    private var mGranted: Boolean = false

    init {
        context.withStyledAttributes(attrs, R.styleable.PermissionView) {
            mTitle = getString(R.styleable.PermissionView_permissionTitle)
            mDescription = getString(R.styleable.PermissionView_permissionDescription)
            mButtonIcon = getDrawable(R.styleable.PermissionView_buttonIcon)
            mButtonText = getString(R.styleable.PermissionView_buttonText)
            mNumber = getInteger(R.styleable.PermissionView_number, mNumber)
            mGranted = getBoolean(R.styleable.PermissionView_granted, mGranted)
        }

        val contentView = View.inflate(getContext(), R.layout.permission_view, null)
        mNumberView = contentView.findViewById(R.id.number)
        mTitleView = contentView.findViewById(R.id.title)
        mDescView = contentView.findViewById(R.id.text)
        mButton = contentView.findViewById(R.id.button)
        addView(contentView)

        setTitle(mTitle)
        setDescription(mDescription)
        setButtonIcon(mButtonIcon)
        setButtonText(mButtonText)
        setGranted(mGranted)
    }

    fun isGranted(): Boolean = mGranted

    fun setGranted(isGranted: Boolean) {
        this.mGranted = isGranted
        updateButton()
    }

    fun setNumber(number: Int) {
        mNumber = number
        mNumberView?.text = number.toString()
    }

    fun setTitle(title: String?) {
        mTitle = title
        mTitleView?.text = title
    }

    fun setDescription(description: String?) {
        mDescription = description
        mDescView?.text = description
    }

    fun setButtonIcon(icon: Drawable?) {
        mButtonIcon = icon
        updateButton()
    }

    fun setButtonText(text: String?) {
        this.mButtonText = text
        updateButton()
    }

    fun setButtonOnClickListener(onClickListener: OnClickListener) {
        mButton?.setOnClickListener(onClickListener)
    }

    private fun updateButton() {
        if (mGranted) {
            mButton?.setIconResource(R.drawable.ic_check_24dp)
            mButton?.setText(R.string.granted)
        } else {
            mButton?.setIcon(mButtonIcon)
            mButton?.text = mButtonText
        }
    }
}
