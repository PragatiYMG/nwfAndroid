package com.app.nwf.ui.fragments

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.nwf.R
import com.app.nwf.databinding.FragmentTranslatorBinding
import com.app.nwf.utils.Constant
import com.app.nwf.utils.GlobalProgressBarUtil
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Locale


class TranslatorFragment : Fragment() {

    var oneLanguage = ""
    var oneLanguageCode = ""
    var oneCountryCode = ""

    var twoLanguage = ""
    var twoLanguageCode = ""
    var twoCountryCode = ""

    private val REQUEST_CODE_SPEECH_INPUT = 100
    private lateinit var textToSpeech: TextToSpeech
    private val modelDownloading = MutableLiveData(false)
    private val translating = MutableLiveData(false)

    private val translators = mutableMapOf<TranslatorOptions, Translator>()
    private var modelDownloadTask: Task<Void>? = null
    private var languageList = Constant.languages

    private lateinit var binding: FragmentTranslatorBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTranslatorBinding.inflate(layoutInflater)

        initView()
        return binding.root
    }

    private fun initView() {

        oneLanguage = languageList[0].first
        oneLanguageCode = languageList[0].second
        oneCountryCode = languageList[0].third

        twoLanguage = languageList[0].first
        twoLanguageCode = languageList[0].second
        twoCountryCode = languageList[0].third

        binding.tfLangMainFrom.setOnClickListener {
            showLanguageDialog(1)
        }

        binding.tfLangMainTo.setOnClickListener {
            showLanguageDialog(2)
        }

        binding.tfSpeakNow.setOnClickListener {
            startSpeechRecognition("$oneLanguageCode-$oneCountryCode")
        }

        binding.tfCopyOne.setOnClickListener {
            copyTextToClip(binding.tfEditOne.text.toString().trim())
        }

        binding.tfCopyTwo.setOnClickListener {
            copyTextToClip(binding.tfTranslate.text.toString().trim())
        }

        binding.tfChange.setOnClickListener {
            var cOne = oneLanguage
            var cTwo= oneLanguageCode
            var cThree = oneCountryCode

            oneLanguage = twoLanguage
            oneLanguageCode = twoLanguageCode
            oneCountryCode = twoCountryCode


            twoLanguage = cOne
            twoLanguageCode = cTwo
            twoCountryCode = cThree

            var sTxt = binding.tfEditOne.text.toString()
            binding.tfEditOne.setText(binding.tfTranslate.text.toString())
            binding.tfTranslate.text = sTxt

            binding.tfLanguageFrom.text = oneLanguage
            binding.tfLanguageTo.text = twoLanguage
        }

        binding.tfVoice.setOnClickListener {
            if(binding.tfTranslate.text.isNotEmpty()){
                ttsToSpeech(binding.tfTranslate.text.toString())
            }else{
                Toast.makeText(requireContext(), "Please translate first", Toast.LENGTH_SHORT).show()
            }
        }

        // Assuming you have already initialized 'translator' and set up model download logic
        binding.tfEditOne.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for this case
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed for this case
            }

            override fun afterTextChanged(s: Editable?) {
                val inputText = s?.toString() ?: return
                translate(inputText, 0)

            }
        })


        // Initialize TextToSpeech
        textToSpeech = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale(twoLanguageCode, twoCountryCode)
            }
        }

    }

    private fun copyTextToClip(textToCopy : String) {
        val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // Create a ClipData object with the text to copy
        val clip = ClipData.newPlainText("label", textToCopy)
        // Set the ClipData to the ClipboardManager
        clipboard.setPrimaryClip(clip)
        // Optionally, show a message indicating that the text has been copied
        Toast.makeText(requireContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }

//    private fun setupLanguageSpinners() {
//        // Example setup for language spinners
//        val languages = arrayOf("English", "French", "Spanish")
//        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, languages)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.languageSpinner1.adapter = adapter
//        binding.languageSpinner2.adapter = adapter
//    }
//
//
//    private fun setupListeners() {
//        binding.voiceIcon.setOnClickListener {
//            startSpeechRecognition()
//        }
//
//        binding.speakerIcon.setOnClickListener {
//            textToSpeech(binding.resultBox.text.toString())
//        }
//    }

    private fun startSpeechRecognition(languageCode: String) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")

        // Set the language for speech recognition
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageCode)
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languageCode)

        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (result != null) {
                val spokenText = result[0]
                binding.tfEditOne.setText(spokenText)
              //  translate(spokenText)
            }
        }
    }

//    private fun translateText(text: String) {
//        translate(text)
//            .addOnSuccessListener { translatedText ->
//                binding.resultBox.text = translatedText
//                textToSpeech(translatedText)
//            }
//            .addOnFailureListener { exception ->
//                binding.resultBox.text = "Translation failed: ${exception.message}"
//            }
//    }

    private fun translate(text: String, xType: Int): Task<String> {

        if (modelDownloading.value == true || translating.value == true) {
            return Tasks.forCanceled()
        }

        val sourceLangCode = TranslateLanguage.fromLanguageTag(oneLanguageCode)
        val targetLangCode = TranslateLanguage.fromLanguageTag(twoLanguageCode)

        if (sourceLangCode == null || targetLangCode == null) {
            return Tasks.forCanceled()
        }

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLangCode)
            .setTargetLanguage(targetLangCode)
            .build()

        val translator = translators[options] ?: Translation.getClient(options)
        translators[options] = translator

        modelDownloading.value = true
        translating.value = true

        // Register watchdog to unblock long-running downloads
       // Handler().postDelayed({ modelDownloading.value = false }, 15000)

        if(xType == 1) {
            GlobalProgressBarUtil.show(requireContext())
        }
        val downloadTask = translator.downloadModelIfNeeded()
        modelDownloadTask = downloadTask

        return downloadTask
            .addOnCompleteListener {
                modelDownloading.value = false
                if (!it.isSuccessful) {
                    translating.value = false
                }else{
                    translate(text, 0)
                }
                if(xType == 1) {
                    GlobalProgressBarUtil.hide()
                }
            }
            .onSuccessTask {
                translator.translate(text)
                    .addOnCompleteListener { translateTask ->
                        translating.value = false
                        if (translateTask.isSuccessful) {
                            binding.tfTranslate.text = translateTask.result
                        }
                        if(xType == 1) {
                            GlobalProgressBarUtil.hide()
                        }
                    }
            }
    }

    private fun ttsToSpeech(text: String) {
        var result = textToSpeech.setLanguage(Locale(twoLanguageCode, twoCountryCode))
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)

//        if(result == -2){
//            Toast.makeText(requireContext(), "Speech Language not supported by your device", Toast.LENGTH_SHORT).show()
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

    private fun showLanguageDialog(type: Int) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.select_language)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.show()

        val mRecycle: RecyclerView = dialog.findViewById(R.id.slRecycle)
        mRecycle.layoutManager = LinearLayoutManager(requireContext())
        mRecycle.adapter = SubjectAdapter(dialog, type)
    }

    inner class SubjectAdapter(var dialog: Dialog, var type: Int) :
        RecyclerView.Adapter<SubjectAdapter.DataHolder>() {

        override fun getItemViewType(position: Int): Int {
            return position
        }

        override fun setHasStableIds(hasStableIds: Boolean) {
            super.setHasStableIds(hasStableIds)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
            return DataHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_subject, parent, false)
            )
        }

        override fun onBindViewHolder(holder: DataHolder, position: Int) {
            holder.mName.text = languageList[position].first

            holder.itemView.setOnClickListener {

                if(type == 1){
                    binding.tfLanguageFrom.text = languageList[position].first
                    oneLanguage = languageList[position].first
                    oneLanguageCode = languageList[position].second
                    oneCountryCode = languageList[position].third
                }else{
                    binding.tfLanguageTo.text = languageList[position].first
                    twoLanguage = languageList[position].first
                    twoLanguageCode = languageList[position].second
                    twoCountryCode = languageList[position].third
                }
                if(binding.tfEditOne.text.isNotEmpty()){
                    translate(binding.tfEditOne.text.toString(), 1)
                }
                dialog.dismiss()
            }
        }

        override fun getItemCount(): Int {
            return languageList.size
        }

        inner class DataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val mName = itemView.findViewById<TextView>(R.id.rsName)!!
        }
    }
}