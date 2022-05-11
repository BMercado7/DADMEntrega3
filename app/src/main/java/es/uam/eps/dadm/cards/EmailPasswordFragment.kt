package es.uam.eps.dadm.cards

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.uam.eps.dadm.cards.database.CardDatabase
import es.uam.eps.dadm.cards.databinding.FragmentEmailpasswordBinding

class EmailPasswordFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentEmailpasswordBinding
    var email: String = "Not logged"
    var pass: String = "Not logged"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<FragmentEmailpasswordBinding>(
            inflater,
            R.layout.fragment_emailpassword,
            container,
            false)
        auth = Firebase.auth

        return binding.root
    }
    override fun onStart() {
        super.onStart()
        // TODO de esta forma solo funciona si no sales de la app, esto es correcto?
        // Guardar datos user
        val userEmailTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                email = s.toString()
            }
        }
        //Ajusta el escuchador al elemento
        binding.emailTextEdit.addTextChangedListener(userEmailTextWatcher)
        val userPassTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                pass = s.toString()
            }
        }
        binding.passwordTextEdit.addTextChangedListener(userPassTextWatcher)

        if(email != "Not logged" && pass != "Not logged"){
            auth.signInWithEmailAndPassword(
                email,
                pass
            )
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        view?.findNavController()
                            ?.navigate(
                                EmailPasswordFragmentDirections
                                    .actionEmailPasswordFragmentToDeckListFragment()
                            )
                    }
                }
        }

        binding.loginButton.setOnClickListener {
            if (binding.emailTextEdit.text.toString()
                    .isValidEmail() && binding.passwordTextEdit.text.toString().isValidPassword()
            ) {
                auth.signInWithEmailAndPassword(
                    binding.emailTextEdit.text.toString(),
                    binding.passwordTextEdit.text.toString()
                )
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Log in success", Toast.LENGTH_SHORT).show()
                            view?.findNavController()
                                ?.navigate(
                                    EmailPasswordFragmentDirections
                                        .actionEmailPasswordFragmentToDeckListFragment()
                                )
                            //updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.createButton.setOnClickListener {
            if (binding.emailTextEdit.text.toString()
                    .isValidEmail() && binding.passwordTextEdit.text.toString().isValidPassword()
            ) {
                // [START sign_in_with_email]
                auth.createUserWithEmailAndPassword(
                    binding.emailTextEdit.text.toString(),
                    binding.passwordTextEdit.text.toString()
                )
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(context, "Create account success", Toast.LENGTH_SHORT)
                                .show()
                            view?.findNavController()
                                ?.navigate(
                                    EmailPasswordFragmentDirections
                                        .actionEmailPasswordFragmentToDeckListFragment()
                                )
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, "Create account failure", Toast.LENGTH_SHORT)
                                .show()
                            //updateUI(null)
                        }
                    }
                // [END sign_in_with_email]
            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(
                    context, "Invalid email or password. Password \n" +
                            "must have more than 6 characters", Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }


    private fun String.isValidEmail() =
        !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    private fun String.isValidPassword() =  !TextUtils.isEmpty(this) && this.length > 5
}