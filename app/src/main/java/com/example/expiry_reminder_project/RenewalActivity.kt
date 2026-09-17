package com.example.expiry_reminder_project

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.expiry_reminder_project.utils.DateUtils
import org.json.JSONArray

class RenewalActivity : AppCompatActivity() {

    private var documentId = ""

    private lateinit var tvDocumentName: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvStatus: TextView

    private lateinit var tvOnlineSteps: TextView
    private lateinit var tvOfflineSteps: TextView
    private lateinit var tvRequired: TextView
    private lateinit var tvTips: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_renewal)

        documentId = intent.getStringExtra("document_id") ?: ""

        initializeViews()
        setupBackButton()
        loadDocument()
    }

    private fun initializeViews() {

        tvDocumentName = findViewById(R.id.tvDocumentName)
        tvCategory = findViewById(R.id.tvCategory)
        tvStatus = findViewById(R.id.tvStatus)

        tvOnlineSteps = findViewById(R.id.tvOnlineSteps)
        tvOfflineSteps = findViewById(R.id.tvOfflineSteps)
        tvRequired = findViewById(R.id.tvRequired)
        tvTips = findViewById(R.id.tvTips)
    }

    private fun setupBackButton() {

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadDocument() {

        val documents = getDocuments()

        for (i in 0 until documents.length()) {

            val document = documents.getJSONObject(i)

            if (document.optString("id") == documentId) {

                val name = document.optString(
                    "name",
                    "Document"
                )

                val category = document.optString(
                    "type",
                    "Other"
                )

                val expiryDate = document.optString(
                    "expiryDate",
                    ""
                )

                tvDocumentName.text = name
                tvCategory.text = category

                val status = DateUtils.getStatus(expiryDate)

                tvStatus.text = status
                updateStatusAppearance(status)

                loadRenewalGuide(name, category)

                return
            }
        }

        // If no document was supplied
        tvDocumentName.text = "Renewal Guidance"
        tvCategory.text = "General"
        tvStatus.text = "Select a document"

        loadRenewalGuide(
            "Document",
            "Other"
        )
    }

    private fun loadRenewalGuide(
        documentName: String,
        category: String
    ) {

        val guide = getRenewalGuide(
            documentName,
            category
        )

        tvOnlineSteps.text = guide.onlineSteps
        tvOfflineSteps.text = guide.offlineSteps
        tvRequired.text = guide.required
        tvTips.text = guide.tips
    }

    private fun getRenewalGuide(
        documentName: String,
        category: String
    ): RenewalGuide {

        val value = (
                documentName + " " + category
                ).lowercase()

        return when {

            value.contains("passport") -> passportGuide()

            value.contains("driving") ||
                    value.contains("licence") ||
                    value.contains("license") -> drivingLicenceGuide()

            value.contains("puc") ||
                    value.contains("pollution") -> pucGuide()

            value.contains("vehicle insurance") ||
                    value.contains("motor insurance") ||
                    value.contains("car insurance") ||
                    value.contains("bike insurance") -> vehicleInsuranceGuide()

            value.contains("health insurance") ||
                    value.contains("life insurance") ||
                    value.contains("medical insurance") -> healthLifeInsuranceGuide()

            value.contains("software") ||
                    value.contains("software licence") ||
                    value.contains("software license") -> softwareLicenceGuide()

            value.contains("income") ||
                    value.contains("ews") -> incomeEwsGuide()

            value.contains("debit") -> debitCardGuide()

            value.contains("credit") -> creditCardGuide()

            else -> categoryBasedGuide(category)
        }
    }

    private fun passportGuide(): RenewalGuide {

        return RenewalGuide(

            onlineSteps = """
1. Open the official Passport Seva portal.

2. Create an account or log in using your existing account.

3. Select "Apply for Fresh Passport / Re-issue of Passport".

4. Select the appropriate reason for passport re-issue.

5. Enter your personal and existing passport details carefully.

6. Complete and submit the application form.

7. Pay the applicable passport service fee.

8. Book an appointment at the required Passport Seva Kendra / Passport Office.

9. Download or save the application receipt.

10. Visit the appointment centre with the required original documents.

11. Complete document verification, photograph and biometrics.

12. Complete police verification if required.

13. Track the application status online.

14. Receive the renewed passport after processing.
""".trimIndent(),

            offlineSteps = """
1. Visit the concerned Passport Seva Kendra / Passport Office.

2. Ask for the passport re-issue/renewal process.

3. Complete the required application form.

4. Carry your existing passport and supporting documents.

5. Submit the application and required documents.

6. Pay the applicable service fee.

7. Complete document verification and biometrics.

8. Complete police verification if required.

9. Collect the acknowledgement/reference number.

10. Track the application status.

11. Receive the renewed passport through the available delivery process.
""".trimIndent(),

            required = """
• Existing passport
• Identity proof
• Address proof, if required
• Supporting documents related to the application
• Passport photographs, if required
• Application/appointment receipt
""".trimIndent(),

            tips = """
• Start the renewal process before the passport expires.

• Check all personal information before submitting.

• Carry original documents for verification.

• Use only the official Passport Seva service.

• Keep the application/reference number safely stored.
""".trimIndent()
        )
    }


    private fun drivingLicenceGuide(): RenewalGuide {

        return RenewalGuide(

            onlineSteps = """
1. Open the official Parivahan website.

2. Select "Online Services".

3. Select "Driving Licence Related Services".

4. Select your state.

5. Select the driving licence renewal service.

6. Enter your existing driving licence details.

7. Verify the displayed information.

8. Fill in the required renewal information.

9. Upload documents if the portal requests them.

10. Pay the applicable renewal fee.

11. Book an appointment if required.

12. Download and save the acknowledgement/receipt.

13. Visit the RTO if physical verification, biometrics or other verification is required.

14. Track the application status online.

15. Receive/download the renewed driving licence according to the available service.
""".trimIndent(),

            offlineSteps = """
1. Visit the concerned Regional Transport Office (RTO).

2. Request the driving licence renewal service.

3. Submit your existing driving licence.

4. Fill in the required renewal form.

5. Submit the required supporting documents.

6. Complete medical/fitness requirements if applicable.

7. Complete photograph, signature or biometric verification if required.

8. Pay the applicable renewal fee.

9. Collect the acknowledgement receipt.

10. Track the application.

11. Receive the renewed driving licence.
""".trimIndent(),

            required = """
• Existing driving licence
• Identity proof
• Address proof
• Passport-size photograph if required
• Medical/fitness certificate if applicable
• Application/acknowledgement receipt
""".trimIndent(),

            tips = """
• Check your licence expiry date before applying.

• Verify your name, date of birth and address.

• Keep your application number safely stored.

• Follow the requirements shown by the selected state/RTO.

• Use the official Parivahan service.
""".trimIndent()
        )
    }

    private fun pucGuide(): RenewalGuide {

        return RenewalGuide(

            onlineSteps = """
1. Open the official transport/PUC service.

2. Enter your vehicle registration details.

3. Check your existing PUC certificate information.

4. Check the certificate expiry date.

5. Locate an authorised PUC testing centre.

6. Take your vehicle to the authorised centre.

7. Complete the required emission test.

8. Pay the applicable testing fee.

9. If the vehicle passes the required emission test, the PUC certificate is issued.

10. Download/save the certificate when the online facility is available.

11. Store the renewed PUC certificate in the application.
""".trimIndent(),

            offlineSteps = """
1. Locate an authorised PUC testing centre.

2. Take the vehicle to the centre.

3. Provide the vehicle registration details.

4. Allow the vehicle to undergo emission testing.

5. Pay the applicable testing fee.

6. If the vehicle satisfies the applicable emission limits, the PUC certificate is issued.

7. Collect the certificate.

8. Keep a digital or physical copy of the certificate.
""".trimIndent(),

            required = """
• Vehicle
• Vehicle registration details
• Existing PUC certificate, if available
• Applicable testing fee
""".trimIndent(),

            tips = """
• Do not wait until the last day of validity.

• PUC generally requires an actual vehicle emission test.

• Use an authorised PUC testing centre.

• Keep the new certificate saved in your app.

• Check the next expiry date after renewal.
""".trimIndent()
        )
    }

    private fun vehicleInsuranceGuide(): RenewalGuide {

        return RenewalGuide(

            onlineSteps = """
1. Identify the insurance company that issued your current vehicle insurance policy.

2. Open the insurer's official website or mobile application.

3. Select Motor Insurance / Car Insurance / Two-Wheeler Insurance.

4. Select the "Renew Policy" option.

5. Enter your existing policy number or vehicle details.

6. Verify the vehicle registration number, policyholder name and vehicle details.

7. Review the existing policy coverage and policy period.

8. Select the renewal policy/coverage offered by the insurer.

9. Check the premium and applicable policy terms carefully.

10. Make the renewal payment through the insurer's official payment facility.

11. Wait for the insurer to confirm successful renewal.

12. Download the renewed motor insurance policy/certificate.

13. Check the new policy number and policy period.

14. Verify the new policy start date and expiry date.

15. Save the renewed policy PDF in this application.

16. Update the document expiry date in Digital Document Expiry Reminder.

17. Keep the renewal receipt/payment confirmation for your records.
""".trimIndent(),

            offlineSteps = """
1. Contact the insurance company that issued your existing vehicle policy.

2. Visit the insurer's branch or contact its authorised agent/intermediary.

3. Carry your existing motor insurance policy.

4. Provide the vehicle registration and policy details.

5. Verify the vehicle information, policyholder details and existing coverage.

6. Ask for the motor insurance renewal quotation.

7. Review the offered coverage, premium, policy period and applicable terms.

8. Complete the insurer's renewal form/process.

9. Pay the renewal premium through the insurer's accepted payment method.

10. Obtain confirmation that the policy has been renewed.

11. Collect or receive the renewed motor insurance policy/certificate.

12. Check the policy number and policy period.

13. Verify the new start date and expiry date.

14. Save a digital copy of the renewed policy in this application.

15. Update the new expiry date in Digital Document Expiry Reminder.

16. Keep the payment receipt and renewed policy safely.
""".trimIndent(),

            required = """
• Existing vehicle insurance policy
• Vehicle registration number
• Vehicle registration certificate (RC), if requested
• Policyholder details
• Vehicle details
• Previous policy details
• Renewal premium/payment
• Other documents requested by the insurer
""".trimIndent(),

            tips = """
• Vehicle insurance renewal is primarily completed with the insurance company, not by renewing the insurance through the RTO.

• Use the insurer's official website, app, branch or authorised channel.

• Check the policy period carefully before completing payment.

• Verify the vehicle registration number and policyholder information.

• After renewal, download and save the NEW insurance policy/certificate.

• Update the new expiry date in this application.

• Keep both the renewed policy and payment confirmation safely stored.

• Never upload or store sensitive payment credentials in this application.
""".trimIndent()
        )
    }

    private fun healthLifeInsuranceGuide(): RenewalGuide {

        return RenewalGuide(

            onlineSteps = """
1. Open the insurer's official website or mobile application.

2. Log in to your policy account.

3. Open the existing health/life insurance policy.

4. Select the renewal option.

5. Verify the policyholder information.

6. Review the coverage and renewal premium.

7. Update information if required.

8. Complete declarations or medical requirements if applicable.

9. Review the renewal details carefully.

10. Pay the renewal premium.

11. Download the renewed policy document.

12. Check the new policy period and expiry date.

13. Save the renewed policy in this application.
""".trimIndent(),

            offlineSteps = """
1. Visit the insurance company's branch or contact an authorised agent.

2. Provide the existing policy details.

3. Verify policyholder information.

4. Discuss the renewal and coverage requirements.

5. Complete the required renewal forms.

6. Complete medical requirements if applicable.

7. Pay the renewal premium.

8. Collect the renewed policy document.

9. Check the policy period and expiry date.

10. Store a digital copy safely.
""".trimIndent(),

            required = """
• Existing insurance policy
• Policyholder identity information
• Address/contact information
• Required supporting documents
• Medical documents if applicable
• Renewal premium
""".trimIndent(),

            tips = """
• Start renewal before the policy expires.

• Carefully check coverage and policy dates.

• Do not ignore insurer notices.

• Keep nominee/contact information updated.

• Save the renewed policy immediately.
""".trimIndent()
        )
    }

    private fun softwareLicenceGuide(): RenewalGuide {

        return RenewalGuide(

            onlineSteps = """
1. Identify the software company/provider that issued your licence.

2. Open the provider's official website or official application.

3. Sign in to the account associated with the software licence.

4. Open the Subscription / Licence / Products section.

5. Select the software licence that is approaching expiry.

6. Check the current licence status and expiry date.

7. Select the available Renew / Extend Subscription option.

8. Select the required renewal period or licence plan.

9. Review the licence terms and renewal price.

10. Complete the payment through the software provider's official payment system.

11. Confirm that the renewal has been successfully completed.

12. Check the new licence/subscription expiry date.

13. If the provider uses a licence key, enter the renewed/new licence key in the software.

14. If the provider uses account-based activation, sign in with the correct account and allow the software to update its licence status.

15. Confirm that the software shows the renewed/active licence.

16. Download/save the renewal invoice, receipt or licence information.

17. Update the new expiry date in Digital Document Expiry Reminder.
""".trimIndent(),

            offlineSteps = """
1. Identify the software vendor or authorised software reseller.

2. Contact the vendor/reseller before the existing licence expires.

3. Provide the existing licence information or purchase information.

4. Tell the vendor the required software/product and renewal period.

5. Confirm the number of users/devices covered by the licence.

6. Select the required renewal plan/licence period.

7. Confirm the renewal price and licence terms.

8. Complete the payment through the vendor or authorised reseller.

9. Receive the renewed licence information.

10. If applicable, receive a new licence key/product key.

11. Enter the new licence key in the software if required.

12. If the licence is account-based, sign in to the correct account.

13. Confirm that the software shows the licence as active/renewed.

14. Obtain the renewal invoice or payment receipt.

15. Save the renewed licence information/receipt in this application.

16. Update the new licence expiry date in Digital Document Expiry Reminder.
""".trimIndent(),

            required = """
• Existing software licence/account
• Software/product name
• Existing licence information
• Registered account details
• Number of users/devices if applicable
• Renewal plan/licence period
• Payment information
• New licence/product key if the provider uses one
• Renewal invoice/receipt
""".trimIndent(),

            tips = """
• Software licence renewal is provider-specific.

• The renewed item may be a subscription extension, renewed licence entitlement or new licence/product key.

• Some software uses account-based activation instead of a physical licence key.

• Use only the software provider's official website or an authorised reseller.

• Check that the software shows the NEW expiry date after renewal.

• Save the renewal invoice or licence information.

• Never share your software password, activation credentials or licence key publicly.

• Update the new expiry date in this application after successful renewal.
""".trimIndent()
        )
    }

    private fun incomeEwsGuide(): RenewalGuide {

        return RenewalGuide(

            onlineSteps = """
1. Open your state's official citizen/e-governance portal.

2. Log in or create an account.

3. Select Income Certificate or EWS Certificate.

4. Select the appropriate application/reissue service.

5. Enter applicant information.

6. Enter the required income/family information.

7. Upload the required supporting documents.

8. Review all information carefully.

9. Submit the application.

10. Pay the applicable service fee if required.

11. Save the application/reference number.

12. Complete verification if requested.

13. Track the application status online.

14. Download the certificate after approval.

15. Save the certificate in this application.
""".trimIndent(),

            offlineSteps = """
1. Visit the appropriate government office or authorised service centre.

2. Ask for the Income/EWS Certificate application process.

3. Complete the application form.

4. Enter applicant and family information.

5. Attach the required supporting documents.

6. Submit the application.

7. Complete document/income verification.

8. Collect the acknowledgement/reference number.

9. Wait for approval.

10. Collect the issued certificate.

11. Store a digital copy in this application.
""".trimIndent(),

            required = """
• Identity proof
• Address/residence proof
• Income-related documents
• Family details
• Required government forms
• Other documents requested by the issuing authority
""".trimIndent(),

            tips = """
• The exact procedure can vary by state and issuing authority.

• Check the validity period of the certificate.

• Keep income information accurate.

• Keep the application/reference number safely stored.

• Use the official state government portal.
""".trimIndent()
        )
    }

    private fun debitCardGuide(): RenewalGuide {

        return RenewalGuide(

            onlineSteps = """
1. Open your bank's official mobile application or net banking.

2. Log in securely.

3. Open Cards / Debit Card Services.

4. Check the card expiry or replacement option.

5. Select Renew/Replace Debit Card if available.

6. Verify your registered address.

7. Submit the renewal/replacement request.

8. Complete OTP or other bank authentication if required.

9. Track the card delivery.

10. Receive the new debit card.

11. Activate the card using the bank's supported method.

12. Set or confirm the PIN if required.

13. Update the new card information where necessary.
""".trimIndent(),

            offlineSteps = """
1. Visit your bank branch.

2. Carry the identification/documents requested by your bank.

3. Request debit card renewal/replacement.

4. Complete the required form.

5. Complete identity verification.

6. Confirm your registered address.

7. Submit the request.

8. Receive the new card according to the bank's process.

9. Activate the card.

10. Set/confirm the PIN if required.
""".trimIndent(),

            required = """
• Existing debit card, if available
• Bank account/customer information
• Identity verification
• Registered mobile number
• Registered address
""".trimIndent(),

            tips = """
• Never store your PIN in this application.

• Never store CVV or OTP.

• Do not share OTP with anyone.

• Use only your bank's official application/website.

• Destroy the old card safely if instructed by your bank.
""".trimIndent()
        )
    }

    private fun creditCardGuide(): RenewalGuide {

        return RenewalGuide(

            onlineSteps = """
1. Open your bank/card issuer's official app or website.

2. Log in securely.

3. Open Credit Card Services.

4. Check the card expiry information.

5. Select the renewal/replacement option if available.

6. Confirm your registered address.

7. Submit the request.

8. Complete OTP/authentication if required.

9. Track the replacement/renewal card.

10. Receive the new card.

11. Activate the new card.

12. Update recurring payments/subscriptions if the card number or expiry changes.

13. Store only safe non-sensitive card information in this application.
""".trimIndent(),

            offlineSteps = """
1. Contact your bank/card issuer or visit the branch if required.

2. Provide the requested identification/card information.

3. Request card renewal/replacement.

4. Complete verification.

5. Confirm your registered address.

6. Submit the request.

7. Receive the new card.

8. Activate the new card.

9. Update recurring payments if necessary.
""".trimIndent(),

            required = """
• Existing credit card
• Customer/account information
• Identity verification if requested
• Registered mobile number
• Registered address
""".trimIndent(),

            tips = """
• Never store the CVV in this application.

• Never store the PIN or OTP.

• Never share OTP with another person.

• Use only the card issuer's official application/website.

• Check recurring payments after receiving a replacement card.
""".trimIndent()
        )
    }

    private fun categoryBasedGuide(
        category: String
    ): RenewalGuide {

        return RenewalGuide(

            onlineSteps = """
1. Identify the organisation/authority that issued the document.

2. Open the issuer's official website or application.

3. Log in or create an account if required.

4. Find the document renewal/reissue service.

5. Enter the existing document details.

6. Complete the renewal application.

7. Upload supporting documents if requested.

8. Pay the applicable fee if required.

9. Submit the application.

10. Save the application/reference number.

11. Complete verification if required.

12. Download the renewed document.

13. Update the new expiry date in this application.
""".trimIndent(),

            offlineSteps = """
1. Identify the office/organisation that issued the document.

2. Visit the concerned office or authorised service centre.

3. Ask for the document renewal/reissue process.

4. Carry the existing document.

5. Complete the required application form.

6. Submit supporting documents.

7. Complete verification if required.

8. Pay the applicable fee.

9. Collect the acknowledgement/reference number.

10. Collect the renewed document after approval.

11. Store a digital copy in this application.
""".trimIndent(),

            required = """
• Existing document
• Identity proof
• Address/contact information
• Supporting documents required by the issuer
• Application/reference number
• Applicable renewal fee
""".trimIndent(),

            tips = """
• Renewal requirements depend on the document and issuing authority.

• Always use the official issuer website.

• Verify the new expiry date after renewal.

• Keep the renewed document stored safely.

• Category detected: $category
""".trimIndent()
        )
    }

    private fun updateStatusAppearance(status: String) {

        when (status.uppercase()) {

            "VALID" -> {
                tvStatus.setTextColor(
                    getColor(R.color.status_valid)
                )
            }

            "EXPIRING SOON" -> {
                tvStatus.setTextColor(
                    getColor(R.color.status_due_soon)
                )
            }

            "EXPIRED" -> {
                tvStatus.setTextColor(
                    getColor(R.color.status_expired)
                )
            }

            else -> {
                tvStatus.setTextColor(
                    getColor(R.color.text_secondary)
                )
            }
        }
    }
    private fun getDocuments(): JSONArray {

        val preferences = getSharedPreferences(
            "DocumentStorage",
            Context.MODE_PRIVATE
        )

        val data = preferences.getString(
            "documents",
            "[]"
        )

        return try {
            JSONArray(data)
        } catch (e: Exception) {
            JSONArray()
        }
    }

    data class RenewalGuide(
        val onlineSteps: String,
        val offlineSteps: String,
        val required: String,
        val tips: String
    )
}