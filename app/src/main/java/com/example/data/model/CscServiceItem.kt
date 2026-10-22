package com.example.data.model

data class CscServiceItem(
    val id: String,
    val titleHi: String,
    val titleEn: String,
    val category: String,
    val descriptionHi: String,
    val descriptionEn: String,
    val requiredDocuments: List<String>,
    val eligibility: String,
    val feeInr: Int,
    val officialUrl: String,
    val iconName: String
)

object CscServiceCatalog {
    val services = listOf(
        CscServiceItem(
            id = "aadhaar",
            titleHi = "Aadhaar Card Services (Download / PVC / Update)",
            titleEn = "Aadhaar Card Service",
            category = "Identity",
            descriptionHi = "Download Aadhaar, address update assistance, biometric appointment booking & official PVC card orders.",
            descriptionEn = "Aadhaar download, update appointment assistance, PVC order & status check.",
            requiredDocuments = listOf(
                "Aadhaar Number or Enrolment Slip (EID)",
                "Registered Mobile Number for OTP",
                "Proof of Identity/Address (for corrections)"
            ),
            eligibility = "All Indian Citizens with valid UIDAI enrollment or Aadhaar number.",
            feeInr = 50,
            officialUrl = "https://myaadhaar.uidai.gov.in",
            iconName = "fingerprint"
        ),
        CscServiceItem(
            id = "pan_card",
            titleHi = "PAN Card (New / Correction / Duplicate)",
            titleEn = "PAN Card (New / Correction)",
            category = "Financial",
            descriptionHi = "Assistance for new PAN card application and correction in name, DOB or father's name via NSDL / UTI.",
            descriptionEn = "New PAN card application & correction assistance via official NSDL / UTI portal.",
            requiredDocuments = listOf(
                "Aadhaar Card Copy",
                "2 Passport Size Photos",
                "Signature Sample",
                "Active Mobile Number & Email"
            ),
            eligibility = "Age 18+ (Minor PAN can also be applied with guardian documents).",
            feeInr = 150,
            officialUrl = "https://www.onlineservices.nsdl.com",
            iconName = "badge"
        ),
        CscServiceItem(
            id = "voter_id",
            titleHi = "Voter ID Card (EPIC Application & Update)",
            titleEn = "Voter ID Card (EPIC)",
            category = "Identity",
            descriptionHi = "New voter card registration (Form 6), correction in voter details (Form 8), and digital e-EPIC download.",
            descriptionEn = "New Voter Registration, voter correction, Aadhaar linking & e-EPIC download.",
            requiredDocuments = listOf(
                "Age Proof (Aadhaar / 10th Certificate)",
                "Address Proof (Electricity Bill / Ration Card)",
                "Recent Passport Size Photograph"
            ),
            eligibility = "Indian citizen completed 18 years of age.",
            feeInr = 50,
            officialUrl = "https://voters.eci.gov.in",
            iconName = "how_to_vote"
        ),
        CscServiceItem(
            id = "income_cert",
            titleHi = "Income Certificate (Aay Praman Patra)",
            titleEn = "Income Certificate",
            category = "Certificates",
            descriptionHi = "Official Tehsil verified Income Certificate application via UP e-District portal for scholarships and welfare.",
            descriptionEn = "Tehsil verified Income Certificate via UP e-District portal.",
            requiredDocuments = listOf(
                "Applicant's Aadhaar Card",
                "Self-Declaration Affidavit",
                "Passport Size Photo",
                "Ration Card or Salary Slip / Land Record"
            ),
            eligibility = "Resident of Uttar Pradesh.",
            feeInr = 60,
            officialUrl = "https://edistrict.up.gov.in",
            iconName = "description"
        ),
        CscServiceItem(
            id = "caste_cert",
            titleHi = "Caste Certificate (Jati Praman Patra)",
            titleEn = "Caste Certificate",
            category = "Certificates",
            descriptionHi = "OBC, SC, ST Caste Certificate application under State and Central Government formats via e-District.",
            descriptionEn = "SC/ST/OBC Caste Certificate application under State and Central format.",
            requiredDocuments = listOf(
                "Aadhaar Card",
                "Family Member's Previous Caste Certificate (if available)",
                "Self-Declaration Form",
                "Passport Size Photo"
            ),
            eligibility = "Belonging to notified reserved communities in UP.",
            feeInr = 60,
            officialUrl = "https://edistrict.up.gov.in",
            iconName = "verified_user"
        ),
        CscServiceItem(
            id = "domicile_cert",
            titleHi = "Domicile / Residence Certificate (Nivas)",
            titleEn = "Domicile Certificate",
            category = "Certificates",
            descriptionHi = "Permanent Resident and Domicile Certificate application for scholarships, admissions & government jobs.",
            descriptionEn = "Permanent Resident certificate for scholarships, admissions & government jobs.",
            requiredDocuments = listOf(
                "Aadhaar Card",
                "Electricity Bill / House Tax Receipt / Ration Card",
                "Verification by Village Pradhan / Ward Member",
                "Passport Size Photo"
            ),
            eligibility = "Living in Uttar Pradesh continuously.",
            feeInr = 60,
            officialUrl = "https://edistrict.up.gov.in",
            iconName = "home"
        ),
        CscServiceItem(
            id = "ayushman_card",
            titleHi = "Ayushman Bharat Golden Card (₹5 Lakh Health)",
            titleEn = "Ayushman Bharat Golden Card",
            category = "Health",
            descriptionHi = "Pradhan Mantri Jan Arogya Yojana (PMJAY) annual ₹5 Lakh cashless hospital treatment card assistance.",
            descriptionEn = "PMJAY 5 Lakh Free Medical Treatment Health Card assistance & eKYC.",
            requiredDocuments = listOf(
                "Ration Card (Eligible Household / Antyodaya)",
                "Aadhaar Card (Linked with mobile for OTP)",
                "Family Members' Aadhaar Details"
            ),
            eligibility = "Listed in SECC 2011 or eligible Ration cardholder in UP.",
            feeInr = 40,
            officialUrl = "https://beneficiary.nha.gov.in",
            iconName = "favorite"
        ),
        CscServiceItem(
            id = "ration_card",
            titleHi = "Ration Card Services (New / Member Addition)",
            titleEn = "Ration Card Services",
            category = "Welfare",
            descriptionHi = "Add new family members, check status, and apply for ration card updates on FCS UP portal.",
            descriptionEn = "Ration card member addition, status tracking & FCS UP assistance.",
            requiredDocuments = listOf(
                "Head of Family Photo & Aadhaar Card",
                "Aadhaar Cards of All Family Members",
                "Bank Passbook (Head of Family)",
                "Income Certificate"
            ),
            eligibility = "Eligible family under Food & Civil Supplies Dept, UP.",
            feeInr = 80,
            officialUrl = "https://fcs.up.gov.in",
            iconName = "shopping_basket"
        ),
        CscServiceItem(
            id = "eshram_card",
            titleHi = "e-Shram Card Registration (UAN Card)",
            titleEn = "e-Shram Card Registration",
            category = "Welfare",
            descriptionHi = "Ministry of Labour Unorganised Workers National Database registration with 12-digit UAN card.",
            descriptionEn = "Ministry of Labour Unorganised Workers National Database Card with UAN.",
            requiredDocuments = listOf(
                "Aadhaar Card",
                "Aadhaar-Linked Active Mobile Number",
                "Bank Account Passbook Details"
            ),
            eligibility = "Unorganised workers aged between 16 and 59 years.",
            feeInr = 40,
            officialUrl = "https://eshram.gov.in",
            iconName = "engineering"
        ),
        CscServiceItem(
            id = "pm_kisan",
            titleHi = "PM Kisan Samman Nidhi (eKYC & Status)",
            titleEn = "PM Kisan Samman Nidhi",
            category = "Agriculture",
            descriptionHi = "PM Kisan installment status check, biometric and OTP eKYC, and farmer registration assistance.",
            descriptionEn = "Installment status, Aadhaar eKYC, land seeding & correction assistance.",
            requiredDocuments = listOf(
                "Aadhaar Card",
                "Khatauni / Land Record (Bhulekh)",
                "Bank Passbook",
                "Active Mobile Number"
            ),
            eligibility = "Landholding farmer families in India.",
            feeInr = 50,
            officialUrl = "https://pmkisan.gov.in",
            iconName = "agriculture"
        ),
        CscServiceItem(
            id = "electricity_bill",
            titleHi = "Electricity Bill Payment (UPPCL Bill)",
            titleEn = "Electricity Bill Payment",
            category = "Utilities",
            descriptionHi = "Instant Rural & Urban UPPCL electricity bill payment with printed computer receipt.",
            descriptionEn = "Rural/Urban UPPCL electricity bill payment with instant computer receipt.",
            requiredDocuments = listOf(
                "10 or 12-digit Electricity Account Number"
            ),
            eligibility = "All electricity consumers.",
            feeInr = 20,
            officialUrl = "https://upenergy.in",
            iconName = "bolt"
        ),
        CscServiceItem(
            id = "online_forms",
            titleHi = "Online Exam & Recruitment Application Forms",
            titleEn = "Online Exam & Job Forms",
            category = "Education",
            descriptionHi = "Assistance for SSC, UP Police, Railway, Scholarship, UPPSC and college entrance examination forms.",
            descriptionEn = "Assistance for SSC, UP Police, Railway, Scholarship & entrance applications.",
            requiredDocuments = listOf(
                "Educational Marksheets (10th/12th/Graduation)",
                "Photo & Signature (Clean scan)",
                "Aadhaar Card & Caste/Domicile Certificates"
            ),
            eligibility = "As specified in recruitment notification.",
            feeInr = 100,
            officialUrl = "https://sarkariresult.com",
            iconName = "assignment"
        )
    )
}
