package ru.netology.network.ui.activity.activity


import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.network.R
import ru.netology.network.databinding.FragmentLoginBinding
import ru.netology.network.ui.activity.util.AndroidUtils.Companion.hideKeyboard
import ru.netology.network.ui.activity.view.afterTextChanged
import ru.netology.network.ui.activity.view.loadCircleCrop
import ru.netology.network.ui.activity.viewmodel.AuthViewModel


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var fragmentBinding: FragmentLoginBinding? = null


    private val viewModelAuth: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLoginBinding.inflate(
            inflater,
            container,
            false
        )
        fragmentBinding = binding

        with(binding) {
            login.requestFocus()

            checkBoxRegister.setOnClickListener {
                name.isVisible = checkBoxRegister.isChecked
                avatarImage.isVisible = checkBoxRegister.isChecked
                avatarImage.setImageResource(R.drawable.avatar)
            }

            login.afterTextChanged {
                viewModelAuth.loginDataChanged(
                    login.text.toString(),
                    password.text.toString()
                )
            }

            password.afterTextChanged {
                viewModelAuth.loginDataChanged(
                    login.text.toString(),
                    password.text.toString()
                )
            }

            val pickPhotoLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    when (it.resultCode) {
                        ImagePicker.RESULT_ERROR -> {
                            Snackbar.make(
                                binding.root,
                                ImagePicker.getError(it.data),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        Activity.RESULT_OK -> {
                            val uri: Uri? = it.data?.data
                            viewModelAuth.changeAvatar(uri, uri?.toFile())
                        }
                    }
                }

            binding.avatarImage.setOnClickListener {
                ImagePicker.with(this@LoginFragment)
                    .crop()
                    .compress(512)
                    .galleryOnly()
                    .galleryMimeTypes(
                        arrayOf(
                            "image/png",
                            "image/jpeg",
                        )
                    )
                    .createIntent(pickPhotoLauncher::launch)
            }

            button.setOnClickListener {
                hideKeyboard(requireView())

                if (checkBoxRegister.isChecked) {
                    viewModelAuth.userRegistration(
                        binding.login.text.toString(),
                        binding.password.text.toString(),
                        binding.name.text.toString()
                    )
                } else {
                    viewModelAuth.userAuthentication(
                        binding.login.text.toString(),
                        binding.password.text.toString()
                    )
                }
            }

            viewModelAuth.loginFormState.observe(viewLifecycleOwner) { state ->
                button.isEnabled = state.isDataValid
                loading.isVisible = state.isLoading
                if (state.isError) {
                    Toast.makeText(context, "Ошибка при авторизации", Toast.LENGTH_LONG)
                        .show()
                }
            }

            viewModelAuth.data.observe(viewLifecycleOwner) {
                if (it.id != 0L)
                    findNavController().navigateUp()
            }

            viewModelAuth.photoAvatar.observe(viewLifecycleOwner) {
                if (it.uri == null) {
                    avatarImage.setImageResource(R.drawable.avatar)
                    return@observe
                }
                avatarImage.loadCircleCrop(it.uri.toString())
            }

            return root
        }
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}