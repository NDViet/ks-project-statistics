import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import truetest.Production.common.addUserRolesAndSortTableData
import truetest.Production.common.enterNumbersInListCardAndSubmit
import truetest.Production.common.fillFormInputsAndSubmitWithTracking
import truetest.Production.common.fillMsuSimulationForm
import truetest.Production.common.interactWithNativeHtmlElements
import truetest.Production.common.interactWithShadowDomAndSelectOptions
import truetest.Production.common.manageUserProfileAndCustomFunctions
import truetest.Production.common.navigateAgGridAndSearchProducts
import truetest.Production.custom.TrueTestScripts



'Initialize test session: Open browser and set view port'

@com.kms.katalon.core.annotation.SetUp
def setup() {
	WebUI.openBrowser('')
	WebUI.setViewPortSize(1920, 1080)
	//WebUI.maximizeWindow()
}

def part1 = {
	"Step 1: Navigate to / with params (clientCode, env)"

	TrueTestScripts.navigate("", ["clientCode": var_clientCode, "env": var_env])

	"Step 2: Fill out various native HTML date input fields and navigate pages"

	interactWithNativeHtmlElements.execute(input_basicDate, input_dateInput, input_dateInputWithRangeRestriction, input_requiredDate)

	"Step 3: Login into Application"

	TrueTestScripts.login()

	"Step 4: Navigate to /forms"

	TrueTestScripts.navigate("forms")

	"Step 5: Complete the MSU simulation form with personal and address details"

	fillMsuSimulationForm.execute(input_addressLine1, input_dateOfBirth, input_legalFirstName, input_legalLastName, input_phone, input_zipCode)

	"Step 6: Hover over button citizenshipStatus"

	WebUI.mouseOver(findTestObject('AI-Generated/Production/Page_msu_simulation_form/button_citizenshipStatus'))

	"Step 7: Click on link sauceLogin -> Navigate to page '/sauce-login'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_msu_simulation_form/link_sauceLogin'))

	"Step 8: Login into Application"

	TrueTestScripts.login()

	"Step 9: Navigate to /sauce-login"

	TrueTestScripts.navigate("sauce-login")

	"Step 10: Click on button addToCart"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart'))

	"Step 11: Click on button addToCart2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart2'))

	"Step 12: Click on button addToCart3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart3'))

	"Step 13: Click on button remove"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_remove'))

	"Step 14: Add user roles and sort table data by headers"

	addUserRolesAndSortTableData.execute(select_status, select_userRole)

	"Step 15: Interact with AgGrid and perform product searches with filters"

	navigateAgGridAndSearchProducts.execute(input_hiddenValue, input_mandatoryField, input_optionalField, input_searchProducts, input_uncontrolledField)

	"Step 16: Fill out form inputs and submit with tracking enabled"

	fillFormInputsAndSubmitWithTracking.execute(input_emailAddress)

	"Step 17: Enter numbers in a list card and submit the data"

	enterNumbersInListCardAndSubmit.execute(input_enterNumber, input_enterNumber2)

	"Step 18: Interact with shadow DOM elements and select options"

	interactWithShadowDomAndSelectOptions.execute(select_formType, select_siteSelection)

	"Step 19: Input data and manage user profile with custom functions and options"

	manageUserProfileAndCustomFunctions.execute(input_doubleQuote, input_functionInput, input_scoreGreaterThan, select_options)

	"Step 20: Enter input value in input xmlNamespace"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_xpath_breaking/input_xmlNamespace'), input_xmlNamespace)

	"Step 21: Select option with input value from select dropdown"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_xpath_breaking/select_dropdown'), select_dropdown, "label", false)

	"Step 22: Click on link dynamicIdLocator -> Navigate to page '/dynamic-id-locator'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_xpath_breaking/link_dynamicIdLocator'))

	"Step 23: Select option with input value from select userRole"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/select_userRole'), select_userRole_1, "label", false)

	"Step 24: Enter input value in input email"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_email'), input_email)

	"Step 25: Enter input value in input text"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_text'), input_text)

	"Step 26: Click on item selectItem"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_selectItem'))

	"Step 27: Click on item item2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item2'))

	"Step 28: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 29: Click on button submitDynamicId"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId'))

	"Step 30: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 31: Click on button submitDynamicId"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId'))

	"Step 32: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 33: Click on button submitDynamicId"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId'))

	"Step 34: Click on link challengingForm -> Navigate to page '/challenging-form'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/link_challengingForm'))

	"Step 35: Enter input value in input fullName"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_fullName'), input_fullName)

	"Step 36: Enter input value in input email"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_email'), input_email_1)

	"Step 37: Enter input value in input phoneNumber"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_phoneNumber'), input_phoneNumber)

	"Step 38: Click on span selectCountry"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectCountry'))

	"Step 39: Click on p singapore"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_singapore'))

	"Step 40: Click on span selectExperienceLevel"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectExperienceLevel'))

	"Step 41: Click on p expertExperience"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_expertExperience'))

	"Step 42: Click on span selectProgrammingLanguage"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectProgrammingLanguage'))

	"Step 43: Click on p selectJava"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_selectJava'))

	"Step 44: Fill out various native HTML date input fields and navigate pages"

	interactWithNativeHtmlElements.execute(input_basicDate_1, input_dateInput_1, input_dateInputWithRangeRestriction_1, input_requiredDate_1)

	"Step 45: Login into Application"

	TrueTestScripts.login()

	"Step 46: Navigate to /forms"

	TrueTestScripts.navigate("forms")

	"Step 47: Complete the MSU simulation form with personal and address details"

	fillMsuSimulationForm.execute(input_addressLine1_1, input_dateOfBirth_1, input_legalFirstName_1, input_legalLastName_1, input_phone_1, input_zipCode_1)

	"Step 48: Hover over button citizenshipStatus"

	WebUI.mouseOver(findTestObject('AI-Generated/Production/Page_msu_simulation_form/button_citizenshipStatus'))

	"Step 49: Click on link sauceLogin -> Navigate to page '/sauce-login'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_msu_simulation_form/link_sauceLogin'))

	"Step 50: Login into Application"

	TrueTestScripts.login()

	"Step 51: Navigate to /sauce-login"

	TrueTestScripts.navigate("sauce-login")

	"Step 52: Click on button addToCart"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart'))

	"Step 53: Click on button addToCart2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart2'))

	"Step 54: Click on button addToCart3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart3'))

	"Step 55: Click on button remove"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_remove'))

	"Step 56: Add user roles and sort table data by headers"

	addUserRolesAndSortTableData.execute(select_status_1, select_userRole_2)

	"Step 57: Interact with AgGrid and perform product searches with filters"

	navigateAgGridAndSearchProducts.execute(input_hiddenValue_1, input_mandatoryField_1, input_optionalField_1, input_searchProducts_1, input_uncontrolledField_1)

	"Step 58: Fill out form inputs and submit with tracking enabled"

	fillFormInputsAndSubmitWithTracking.execute(input_emailAddress_1)

	"Step 59: Enter numbers in a list card and submit the data"

	enterNumbersInListCardAndSubmit.execute(input_enterNumber_1, input_enterNumber2_1)

	"Step 60: Interact with shadow DOM elements and select options"

	interactWithShadowDomAndSelectOptions.execute(select_formType_1, select_siteSelection_1)

	"Step 61: Input data and manage user profile with custom functions and options"

	manageUserProfileAndCustomFunctions.execute(input_doubleQuote_1, input_functionInput_1, input_scoreGreaterThan_1, select_options_1)

	"Step 62: Enter input value in input xmlNamespace"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_xpath_breaking/input_xmlNamespace'), input_xmlNamespace_1)

	"Step 63: Select option with input value from select dropdown"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_xpath_breaking/select_dropdown'), select_dropdown_1, "label", false)

	"Step 64: Click on link dynamicIdLocator -> Navigate to page '/dynamic-id-locator'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_xpath_breaking/link_dynamicIdLocator'))

	"Step 65: Select option with input value from select userRole"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/select_userRole'), select_userRole_18, "label", false)

	"Step 66: Enter input value in input email"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_email'), input_email_8)

	"Step 67: Enter input value in input enterText"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_enterText'), input_enterText)

	"Step 68: Click on item item1"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item1'))

	"Step 69: Click on item item2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item2'))

	"Step 70: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 71: Click on button submitDynamicId2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId2'))

	"Step 72: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 73: Click on button submitDynamicId2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId2'))

	"Step 74: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 75: Click on button submitDynamicId2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId2'))

	"Step 76: Click on link challengingForm -> Navigate to page '/challenging-form'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/link_challengingForm'))

	"Step 77: Enter input value in input fullName2"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_fullName2'), input_fullName2)

	"Step 78: Enter input value in input email2"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_email2'), input_email2)

	"Step 79: Enter input value in input enterPhoneNumber"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_enterPhoneNumber'), input_enterPhoneNumber)

	"Step 80: Click on span selectCountry2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectCountry2'))

	"Step 81: Click on p selectSingapore"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_selectSingapore'))

	"Step 82: Click on span selectExperienceLevel2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectExperienceLevel2'))

	"Step 83: Click on p expertOption"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_expertOption'))

	"Step 84: Click on span selectProgrammingLanguage2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectProgrammingLanguage2'))

	"Step 85: Click on p java -> Navigate to page '/native-element'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_java'))

	"Step 86: Enter input value in input basicDate"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_basicDate'), input_basicDate_2)

	"Step 87: Enter input value in input dateInput"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_dateInput'), input_dateInput_2)

	"Step 88: Enter input value in input dateInputWithRangeRestriction"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_dateInputWithRangeRestriction'), input_dateInputWithRangeRestriction_2)

	"Step 89: Enter input value in input requiredDate"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_requiredDate'), input_requiredDate_2)

	"Step 90: Click on link about -> Navigate to page '/about'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_native_element/link_about'))

	"Step 91: Click on link navigateForms -> Navigate to page '/forms'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_about/link_forms'))

	"Step 92: Login into Application"

	TrueTestScripts.login()

	"Step 93: Navigate to /forms"

	TrueTestScripts.navigate("forms")

	"Step 94: Complete the MSU simulation form with personal and address details"

	fillMsuSimulationForm.execute(input_addressLine1_2, input_dateOfBirth_2, input_legalFirstName_2, input_legalLastName_2, input_phone_2, input_zipCode_2)

	"Step 95: Hover over button citizenshipStatus"

	WebUI.mouseOver(findTestObject('AI-Generated/Production/Page_msu_simulation_form/button_citizenshipStatus'))

	"Step 96: Click on link sauceLogin -> Navigate to page '/sauce-login'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_msu_simulation_form/link_sauceLogin'))

	"Step 97: Login into Application"

	TrueTestScripts.login()

	"Step 98: Navigate to /sauce-login"

	TrueTestScripts.navigate("sauce-login")

	"Step 99: Click on button addToCart"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart'))

	"Step 100: Click on button addToCart2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart2'))

	"Step 101: Click on button addToCart3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart3'))

	"Step 102: Click on button remove"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_remove'))

	"Step 103: Add user roles and sort table data by headers"

	addUserRolesAndSortTableData.execute(select_status_2, select_userRole_3)

	"Step 104: Interact with AgGrid and perform product searches with filters"

	navigateAgGridAndSearchProducts.execute(input_hiddenValue_2, input_mandatoryField_2, input_optionalField_2, input_searchProducts_2, input_uncontrolledField_2)

	"Step 105: Fill out form inputs and submit with tracking enabled"

	fillFormInputsAndSubmitWithTracking.execute(input_emailAddress_2)

	"Step 106: Enter numbers in a list card and submit the data"

	enterNumbersInListCardAndSubmit.execute(input_enterNumber_2, input_enterNumber2_2)

	"Step 107: Interact with shadow DOM elements and select options"

	interactWithShadowDomAndSelectOptions.execute(select_formType_2, select_siteSelection_2)

	"Step 108: Input data and manage user profile with custom functions and options"

	manageUserProfileAndCustomFunctions.execute(input_doubleQuote_2, input_functionInput_2, input_scoreGreaterThan_2, select_options_2)

	"Step 109: Hover over button nightmareElement"

	WebUI.mouseOver(findTestObject('AI-Generated/Production/Page_xpath_breaking/button_nightmareElement'))

	"Step 110: Enter input value in input xmlNamespace"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_xpath_breaking/input_xmlNamespace'), input_xmlNamespace_8)

	"Step 111: Select option with input value from select dropdown"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_xpath_breaking/select_dropdown'), select_dropdown_8, "label", false)

	"Step 112: Click on link dynamicIdLocator -> Navigate to page '/dynamic-id-locator'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_xpath_breaking/link_dynamicIdLocator'))

	"Step 113: Select option with input value from select userRole"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/select_userRole'), select_userRole_24, "label", false)

	"Step 114: Enter input value in input email"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_email'), input_email_9)

	"Step 115: Enter input value in input enterText2"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_enterText2'), input_enterText2)

	"Step 116: Click on item item12"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item12'))

	"Step 117: Click on item item2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item2'))

	"Step 118: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 119: Click on button submitDynamicId3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId3'))

	"Step 120: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 121: Click on button submitDynamicId3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId3'))

	"Step 122: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 123: Click on button submitDynamicId3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId3'))

	"Step 124: Click on link challengingForm -> Navigate to page '/challenging-form'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/link_challengingForm'))

	"Step 125: Enter input value in input fullName3"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_fullName3'), input_fullName3)

	"Step 126: Enter input value in input email3"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_email3'), input_email3)

	"Step 127: Enter input value in input enterPhoneNumber2"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_enterPhoneNumber2'), input_enterPhoneNumber2)

	"Step 128: Click on span selectCountry3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectCountry3'))

	"Step 129: Click on p singapore2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_singapore2'))

	"Step 130: Click on span selectExperienceLevel3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectExperienceLevel3'))

	"Step 131: Click on p expertExperience2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_expertExperience2'))

	"Step 132: Click on span selectProgrammingLanguage3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectProgrammingLanguage3'))

	"Step 133: Click on p java2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_java2'))

	"Step 134: Fill out various native HTML date input fields and navigate pages"

	interactWithNativeHtmlElements.execute(input_basicDate_3, input_dateInput_3, input_dateInputWithRangeRestriction_3, input_requiredDate_3)

	"Step 135: Login into Application"

	TrueTestScripts.login()

	"Step 136: Navigate to /forms"

	TrueTestScripts.navigate("forms")

	"Step 137: Complete the MSU simulation form with personal and address details"

	fillMsuSimulationForm.execute(input_addressLine1_3, input_dateOfBirth_3, input_legalFirstName_3, input_legalLastName_3, input_phone_3, input_zipCode_3)

	"Step 138: Click on link sauceLogin -> Navigate to page '/sauce-login'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_msu_simulation_form/link_sauceLogin'))

	"Step 139: Login into Application"

	TrueTestScripts.login()

	"Step 140: Navigate to /sauce-login"

	TrueTestScripts.navigate("sauce-login")

	"Step 141: Click on button addToCart"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart'))

	"Step 142: Click on button addToCart2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart2'))

	"Step 143: Click on button addToCart3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart3'))

	"Step 144: Click on button remove"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_remove'))

	"Step 145: Add user roles and sort table data by headers"

	addUserRolesAndSortTableData.execute(select_status_3, select_userRole_4)

	"Step 146: Interact with AgGrid and perform product searches with filters"

	navigateAgGridAndSearchProducts.execute(input_hiddenValue_3, input_mandatoryField_3, input_optionalField_3, input_searchProducts_3, input_uncontrolledField_3)

	"Step 147: Fill out form inputs and submit with tracking enabled"

	fillFormInputsAndSubmitWithTracking.execute(input_emailAddress_3)

	"Step 148: Enter numbers in a list card and submit the data"

	enterNumbersInListCardAndSubmit.execute(input_enterNumber_3, input_enterNumber2_3)

	"Step 149: Interact with shadow DOM elements and select options"

	interactWithShadowDomAndSelectOptions.execute(select_formType_3, select_siteSelection_3)

	"Step 150: Input data and manage user profile with custom functions and options"

	manageUserProfileAndCustomFunctions.execute(input_doubleQuote_3, input_functionInput_3, input_scoreGreaterThan_3, select_options_3)

	"Step 151: Enter input value in input xmlNamespace"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_xpath_breaking/input_xmlNamespace'), input_xmlNamespace_9)

	"Step 152: Select option with input value from select dropdown"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_xpath_breaking/select_dropdown'), select_dropdown_9, "label", false)

	"Step 153: Click on link dynamicIdLocator -> Navigate to page '/dynamic-id-locator'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_xpath_breaking/link_dynamicIdLocator'))

	"Step 154: Select option with input value from select userRole"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/select_userRole'), select_userRole_25, "label", false)

	"Step 155: Enter input value in input email"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_email'), input_email_10)

	"Step 156: Enter input value in input enterText3"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_enterText3'), input_enterText3)

	"Step 157: Click on item item13"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item13'))

	"Step 158: Click on item item2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item2'))

	"Step 159: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 160: Click on button submitDynamicId4"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId4'))

	"Step 161: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 162: Click on button submitDynamicId4"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId4'))

	"Step 163: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 164: Click on button submitDynamicId4"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId4'))

	"Step 165: Click on link challengingForm -> Navigate to page '/challenging-form'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/link_challengingForm'))

	"Step 166: Enter input value in input fullName4"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_fullName4'), input_fullName4)

	"Step 167: Enter input value in input enterEmail"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_enterEmail'), input_enterEmail)

	"Step 168: Enter input value in input phoneNumber2"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_phoneNumber2'), input_phoneNumber2)

	"Step 169: Click on span selectCountry4"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectCountry4'))

	"Step 170: Click on p singapore3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_singapore3'))

	"Step 171: Click on span selectExperienceLevel4"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectExperienceLevel4'))

	"Step 172: Click on p expertExperience3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_expertExperience3'))

	"Step 173: Click on span selectProgrammingLanguage4"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectProgrammingLanguage4'))

	"Step 174: Click on p java3 -> Navigate to page '/native-element'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_java3'))

	"Step 175: Enter input value in input basicDate"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_basicDate'), input_basicDate_10)

	"Step 176: Enter input value in input dateInput"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_dateInput'), input_dateInput_10)

	"Step 177: Enter input value in input dateInputWithRangeRestriction"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_dateInputWithRangeRestriction'), input_dateInputWithRangeRestriction_10)

	"Step 178: Enter input value in input requiredDate"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_requiredDate'), input_requiredDate_10)

	"Step 179: Click on link about -> Navigate to page '/about'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_native_element/link_about'))

	"Step 180: Click on link navigateForms -> Navigate to page '/forms'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_about/link_forms'))

	"Step 181: Login into Application"

	TrueTestScripts.login()

	"Step 182: Navigate to /forms"

	TrueTestScripts.navigate("forms")

	"Step 183: Complete the MSU simulation form with personal and address details"

	fillMsuSimulationForm.execute(input_addressLine1_4, input_dateOfBirth_4, input_legalFirstName_4, input_legalLastName_4, input_phone_4, input_zipCode_4)

	"Step 184: Hover over button citizenshipStatus"

	WebUI.mouseOver(findTestObject('AI-Generated/Production/Page_msu_simulation_form/button_citizenshipStatus'))

	"Step 185: Click on link sauceLogin -> Navigate to page '/sauce-login'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_msu_simulation_form/link_sauceLogin'))

	"Step 186: Login into Application"

	TrueTestScripts.login()

	"Step 187: Navigate to /sauce-login"

	TrueTestScripts.navigate("sauce-login")

	"Step 188: Click on button addToCart"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart'))

	"Step 189: Click on button addToCart2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart2'))

	"Step 190: Click on button addToCart3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart3'))

	"Step 191: Click on button remove"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_remove'))

	"Step 192: Add user roles and sort table data by headers"

	addUserRolesAndSortTableData.execute(select_status_4, select_userRole_5)

	"Step 193: Interact with AgGrid and perform product searches with filters"

	navigateAgGridAndSearchProducts.execute(input_hiddenValue_4, input_mandatoryField_4, input_optionalField_4, input_searchProducts_4, input_uncontrolledField_4)

	"Step 194: Fill out form inputs and submit with tracking enabled"

	fillFormInputsAndSubmitWithTracking.execute(input_emailAddress_4)

	"Step 195: Enter numbers in a list card and submit the data"

	enterNumbersInListCardAndSubmit.execute(input_enterNumber_4, input_enterNumber2_4)

	"Step 196: Interact with shadow DOM elements and select options"

	interactWithShadowDomAndSelectOptions.execute(select_formType_4, select_siteSelection_4)

	"Step 197: Input data and manage user profile with custom functions and options"

	manageUserProfileAndCustomFunctions.execute(input_doubleQuote_4, input_functionInput_4, input_scoreGreaterThan_4, select_options_4)

	"Step 198: Hover over button nightmareElement"

	WebUI.mouseOver(findTestObject('AI-Generated/Production/Page_xpath_breaking/button_nightmareElement'))

	"Step 199: Enter input value in input xmlNamespace"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_xpath_breaking/input_xmlNamespace'), input_xmlNamespace_10)

	"Step 200: Select option with input value from select dropdown"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_xpath_breaking/select_dropdown'), select_dropdown_10, "label", false)

	"Step 201: Click on link xpathBreakingCharacters"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_xpath_breaking/link_xpathBreakingCharacters'))

	"Step 202: Fill out various native HTML date input fields and navigate pages"

	interactWithNativeHtmlElements.execute(input_basicDate_4, input_dateInput_4, input_dateInputWithRangeRestriction_4, input_requiredDate_4)

	"Step 203: Login into Application"

	TrueTestScripts.login()

	"Step 204: Navigate to /forms"

	TrueTestScripts.navigate("forms")

	"Step 205: Complete the MSU simulation form with personal and address details"

	fillMsuSimulationForm.execute(input_addressLine1_5, input_dateOfBirth_5, input_legalFirstName_5, input_legalLastName_5, input_phone_5, input_zipCode_5)

	"Step 206: Hover over button citizenshipStatus"

	WebUI.mouseOver(findTestObject('AI-Generated/Production/Page_msu_simulation_form/button_citizenshipStatus'))

	"Step 207: Click on link sauceLogin -> Navigate to page '/sauce-login'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_msu_simulation_form/link_sauceLogin'))

	"Step 208: Login into Application"

	TrueTestScripts.login()

	"Step 209: Navigate to /sauce-login"

	TrueTestScripts.navigate("sauce-login")

	"Step 210: Click on button addToCart"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart'))

	"Step 211: Click on button addToCart2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart2'))

	"Step 212: Click on button addToCart3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart3'))

	"Step 213: Click on button remove"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_remove'))

	"Step 214: Add user roles and sort table data by headers"

	addUserRolesAndSortTableData.execute(select_status_5, select_userRole_6)

	"Step 215: Interact with AgGrid and perform product searches with filters"

	navigateAgGridAndSearchProducts.execute(input_hiddenValue_5, input_mandatoryField_5, input_optionalField_5, input_searchProducts_5, input_uncontrolledField_5)

	"Step 216: Fill out form inputs and submit with tracking enabled"

	fillFormInputsAndSubmitWithTracking.execute(input_emailAddress_5)

	"Step 217: Enter numbers in a list card and submit the data"

	enterNumbersInListCardAndSubmit.execute(input_enterNumber_5, input_enterNumber2_5)

	"Step 218: Interact with shadow DOM elements and select options"

	interactWithShadowDomAndSelectOptions.execute(select_formType_5, select_siteSelection_5)

	"Step 219: Input data and manage user profile with custom functions and options"

	manageUserProfileAndCustomFunctions.execute(input_doubleQuote_5, input_functionInput_5, input_scoreGreaterThan_5, select_options_5)

	"Step 220: Hover over button nightmareElement"

	WebUI.mouseOver(findTestObject('AI-Generated/Production/Page_xpath_breaking/button_nightmareElement'))

	"Step 221: Enter input value in input xmlNamespace"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_xpath_breaking/input_xmlNamespace'), input_xmlNamespace_11)

	"Step 222: Select option with input value from select dropdown"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_xpath_breaking/select_dropdown'), select_dropdown_11, "label", false)

	"Step 223: Click on link dynamicIdLocator -> Navigate to page '/dynamic-id-locator'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_xpath_breaking/link_dynamicIdLocator'))

	"Step 224: Select option with input value from select userRole"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/select_userRole'), select_userRole_26, "label", false)

	"Step 225: Enter input value in input email"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_email'), input_email_11)

	"Step 226: Enter input value in input textInput"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_textInput'), input_textInput)

	"Step 227: Click on item item14"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item14'))

	"Step 228: Click on item item2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item2'))

	"Step 229: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 230: Click on button submitDynamicId5"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId5'))

	"Step 231: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 232: Click on button submitDynamicId6"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId6'))

	"Step 233: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 234: Click on button submitDynamicId6"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId6'))

	"Step 235: Click on link challengingForm -> Navigate to page '/challenging-form'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/link_challengingForm'))

	"Step 236: Enter input value in input enterFullName"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_enterFullName'), input_enterFullName)

	"Step 237: Enter input value in input email4"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_email4'), input_email4)

	"Step 238: Enter input value in input phoneNumber3"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_phoneNumber3'), input_phoneNumber3)

	"Step 239: Click on span selectCountry5"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectCountry5'))

	"Step 240: Click on p singapore4"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_singapore4'))

	"Step 241: Click on span selectExperienceLevel5"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectExperienceLevel5'))

	"Step 242: Click on p expertOption2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_expertOption2'))

	"Step 243: Click on span selectProgrammingLanguage5"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectProgrammingLanguage5'))

	"Step 244: Click on p java4 -> Navigate to page '/native-element'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_java4'))

	"Step 245: Enter input value in input basicDate"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_basicDate'), input_basicDate_11)

	"Step 246: Enter input value in input dateInput"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_dateInput'), input_dateInput_11)

	"Step 247: Enter input value in input dateInputWithRangeRestriction"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_dateInputWithRangeRestriction'), input_dateInputWithRangeRestriction_11)

	"Step 248: Enter input value in input requiredDate"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_requiredDate'), input_requiredDate_11)

	"Step 249: Click on link about -> Navigate to page '/about'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_native_element/link_about'))

	"Step 250: Click on link navigateForms -> Navigate to page '/forms'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_about/link_forms'))

	"Step 251: Login into Application"

	TrueTestScripts.login()

	"Step 252: Navigate to /forms"

	TrueTestScripts.navigate("forms")

	"Step 253: Complete the MSU simulation form with personal and address details"

	fillMsuSimulationForm.execute(input_addressLine1_6, input_dateOfBirth_6, input_legalFirstName_6, input_legalLastName_6, input_phone_6, input_zipCode_6)

	"Step 254: Click on link sauceLogin -> Navigate to page '/sauce-login'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_msu_simulation_form/link_sauceLogin'))

	"Step 255: Login into Application"

	TrueTestScripts.login()

	"Step 256: Navigate to /sauce-login"

	TrueTestScripts.navigate("sauce-login")

	"Step 257: Click on button addToCart"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart'))

	"Step 258: Click on button addToCart2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart2'))

	"Step 259: Click on button addToCart3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart3'))

	"Step 260: Click on button remove"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_remove'))

	"Step 261: Add user roles and sort table data by headers"

	addUserRolesAndSortTableData.execute(select_status_6, select_userRole_7)

	"Step 262: Interact with AgGrid and perform product searches with filters"

	navigateAgGridAndSearchProducts.execute(input_hiddenValue_6, input_mandatoryField_6, input_optionalField_6, input_searchProducts_6, input_uncontrolledField_6)

	"Step 263: Fill out form inputs and submit with tracking enabled"

	fillFormInputsAndSubmitWithTracking.execute(input_emailAddress_6)

	"Step 264: Enter numbers in a list card and submit the data"

	enterNumbersInListCardAndSubmit.execute(input_enterNumber_6, input_enterNumber2_6)

	"Step 265: Interact with shadow DOM elements and select options"

	interactWithShadowDomAndSelectOptions.execute(select_formType_6, select_siteSelection_6)

	"Step 266: Input data and manage user profile with custom functions and options"

	manageUserProfileAndCustomFunctions.execute(input_doubleQuote_6, input_functionInput_6, input_scoreGreaterThan_6, select_options_6)

	"Step 267: Enter input value in input xmlNamespace"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_xpath_breaking/input_xmlNamespace'), input_xmlNamespace_12)

	"Step 268: Select option with input value from select dropdown"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_xpath_breaking/select_dropdown'), select_dropdown_12, "label", false)

	"Step 269: Click on link dynamicIdLocator -> Navigate to page '/dynamic-id-locator'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_xpath_breaking/link_dynamicIdLocator'))

	"Step 270: Select option with input value from select userRole"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/select_userRole'), select_userRole_27, "label", false)

	"Step 271: Enter input value in input email"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_email'), input_email_12)

	"Step 272: Enter input value in input enterText4"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_enterText4'), input_enterText4)

	"Step 273: Click on item item15"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item15'))

	"Step 274: Click on item item2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item2'))

	"Step 275: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 276: Click on button submitDynamicId7"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId7'))

	"Step 277: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 278: Click on button submitDynamicId7"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId7'))

	"Step 279: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 280: Click on button submitDynamicId7"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId7'))

	"Step 281: Click on link challengingForm -> Navigate to page '/challenging-form'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/link_challengingForm'))

	"Step 282: Enter input value in input fullName5"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_fullName5'), input_fullName5)

	"Step 283: Enter input value in input email5"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_email5'), input_email5)

	"Step 284: Enter input value in input phoneNumber4"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_phoneNumber4'), input_phoneNumber4)

	"Step 285: Click on span selectCountry6"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectCountry6'))

	"Step 286: Click on p singapore5"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_singapore5'))

	"Step 287: Click on span selectExperienceLevel6"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectExperienceLevel6'))

	"Step 288: Click on p expertExperience4"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_expertExperience4'))

	"Step 289: Click on span selectProgrammingLanguage6"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectProgrammingLanguage6'))

	"Step 290: Click on p java5"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_java5'))

	"Step 291: Fill out various native HTML date input fields and navigate pages"

	interactWithNativeHtmlElements.execute(input_basicDate_5, input_dateInput_5, input_dateInputWithRangeRestriction_5, input_requiredDate_5)

	"Step 292: Login into Application"

	TrueTestScripts.login()

	"Step 293: Navigate to /forms"

	TrueTestScripts.navigate("forms")

	"Step 294: Complete the MSU simulation form with personal and address details"

	fillMsuSimulationForm.execute(input_addressLine1_7, input_dateOfBirth_7, input_legalFirstName_7, input_legalLastName_7, input_phone_7, input_zipCode_7)

	"Step 295: Hover over button citizenshipStatus"

	WebUI.mouseOver(findTestObject('AI-Generated/Production/Page_msu_simulation_form/button_citizenshipStatus'))

	"Step 296: Click on link sauceLogin -> Navigate to page '/sauce-login'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_msu_simulation_form/link_sauceLogin'))

	"Step 297: Login into Application"

	TrueTestScripts.login()

	"Step 298: Navigate to /sauce-login"

	TrueTestScripts.navigate("sauce-login")

	"Step 299: Click on button addToCart"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart'))

	"Step 300: Click on button addToCart2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart2'))

}

def part2 = {
	"Step 301: Click on button addToCart3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart3'))

	"Step 302: Click on button remove"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_remove'))

	"Step 303: Add user roles and sort table data by headers"

	addUserRolesAndSortTableData.execute(select_status_7, select_userRole_8)

	"Step 304: Interact with AgGrid and perform product searches with filters"

	navigateAgGridAndSearchProducts.execute(input_hiddenValue_7, input_mandatoryField_7, input_optionalField_7, input_searchProducts_7, input_uncontrolledField_7)

	"Step 305: Fill out form inputs and submit with tracking enabled"

	fillFormInputsAndSubmitWithTracking.execute(input_emailAddress_7)

	"Step 306: Enter numbers in a list card and submit the data"

	enterNumbersInListCardAndSubmit.execute(input_enterNumber_7, input_enterNumber2_7)

	"Step 307: Interact with shadow DOM elements and select options"

	interactWithShadowDomAndSelectOptions.execute(select_formType_7, select_siteSelection_7)

	"Step 308: Input data and manage user profile with custom functions and options"

	manageUserProfileAndCustomFunctions.execute(input_doubleQuote_7, input_functionInput_7, input_scoreGreaterThan_7, select_options_7)

	"Step 309: Enter input value in input xmlNamespace"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_xpath_breaking/input_xmlNamespace'), input_xmlNamespace_13)

	"Step 310: Select option with input value from select dropdown"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_xpath_breaking/select_dropdown'), select_dropdown_13, "label", false)

	"Step 311: Click on link dynamicIdLocator -> Navigate to page '/dynamic-id-locator'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_xpath_breaking/link_dynamicIdLocator'))

	"Step 312: Select option with input value from select userRole"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/select_userRole'), select_userRole_28, "label", false)

	"Step 313: Enter input value in input email"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_email'), input_email_13)

	"Step 314: Enter input value in input enterText5"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_enterText5'), input_enterText5)

	"Step 315: Click on item item16"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item16'))

	"Step 316: Click on item item2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item2'))

	"Step 317: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 318: Click on button submitDynamicId8"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId8'))

	"Step 319: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 320: Click on button submitDynamicId9"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId9'))

	"Step 321: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 322: Click on button submitDynamicId9"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId9'))

	"Step 323: Click on link challengingForm -> Navigate to page '/challenging-form'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/link_challengingForm'))

	"Step 324: Enter input value in input fullName6"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_fullName6'), input_fullName6)

	"Step 325: Enter input value in input email6"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_email6'), input_email6)

	"Step 326: Enter input value in input phoneNumber5"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_phoneNumber5'), input_phoneNumber5)

	"Step 327: Click on span selectCountry7"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectCountry7'))

	"Step 328: Click on p singapore6"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_singapore6'))

	"Step 329: Click on span selectExperienceLevel7"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectExperienceLevel7'))

	"Step 330: Click on p expertExperience5"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_expertExperience5'))

	"Step 331: Click on span selectProgrammingLanguage7"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectProgrammingLanguage7'))

	"Step 332: Click on p javaOption -> Navigate to page '/native-element'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_javaOption'))

	"Step 333: Enter input value in input basicDate"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_basicDate'), input_basicDate_12)

	"Step 334: Enter input value in input dateInput"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_dateInput'), input_dateInput_12)

	"Step 335: Enter input value in input dateInputWithRangeRestriction"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_dateInputWithRangeRestriction'), input_dateInputWithRangeRestriction_12)

	"Step 336: Enter input value in input requiredDate"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_requiredDate'), input_requiredDate_12)

	"Step 337: Click on link about -> Navigate to page '/about'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_native_element/link_about'))

	"Step 338: Click on link navigateForms -> Navigate to page '/forms'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_about/link_forms'))

	"Step 339: Login into Application"

	TrueTestScripts.login()

	"Step 340: Navigate to /forms"

	TrueTestScripts.navigate("forms")

	"Step 341: Complete the MSU simulation form with personal and address details"

	fillMsuSimulationForm.execute(input_addressLine1_8, input_dateOfBirth_8, input_legalFirstName_8, input_legalLastName_8, input_phone_8, input_zipCode_8)

	"Step 342: Hover over button citizenshipStatus"

	WebUI.mouseOver(findTestObject('AI-Generated/Production/Page_msu_simulation_form/button_citizenshipStatus'))

	"Step 343: Click on link sauceLogin -> Navigate to page '/sauce-login'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_msu_simulation_form/link_sauceLogin'))

	"Step 344: Login into Application"

	TrueTestScripts.login()

	"Step 345: Navigate to /sauce-login"

	TrueTestScripts.navigate("sauce-login")

	"Step 346: Click on button addToCart"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart'))

	"Step 347: Click on button addToCart2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart2'))

	"Step 348: Click on button addToCart3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart3'))

	"Step 349: Click on button remove"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_remove'))

	"Step 350: Add user roles and sort table data by headers"

	addUserRolesAndSortTableData.execute(select_status_8, select_userRole_9)

	"Step 351: Interact with AgGrid and perform product searches with filters"

	navigateAgGridAndSearchProducts.execute(input_hiddenValue_8, input_mandatoryField_8, input_optionalField_8, input_searchProducts_8, input_uncontrolledField_8)

	"Step 352: Fill out form inputs and submit with tracking enabled"

	fillFormInputsAndSubmitWithTracking.execute(input_emailAddress_8)

	"Step 353: Enter numbers in a list card and submit the data"

	enterNumbersInListCardAndSubmit.execute(input_enterNumber_8, input_enterNumber2_8)

	"Step 354: Interact with shadow DOM elements and select options"

	interactWithShadowDomAndSelectOptions.execute(select_formType_8, select_siteSelection_8)

	"Step 355: Input data and manage user profile with custom functions and options"

	manageUserProfileAndCustomFunctions.execute(input_doubleQuote_8, input_functionInput_8, input_scoreGreaterThan_8, select_options_8)

	"Step 356: Enter input value in input xmlNamespace"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_xpath_breaking/input_xmlNamespace'), input_xmlNamespace_14)

	"Step 357: Select option with input value from select dropdown"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_xpath_breaking/select_dropdown'), select_dropdown_14, "label", false)

	"Step 358: Click on link dynamicIdLocator -> Navigate to page '/dynamic-id-locator'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_xpath_breaking/link_dynamicIdLocator'))

	"Step 359: Select option with input value from select userRole"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/select_userRole'), select_userRole_29, "label", false)

	"Step 360: Enter input value in input email"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_email'), input_email_14)

	"Step 361: Enter input value in input text2"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_text2'), input_text2)

	"Step 362: Click on item selectItem2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_selectItem2'))

	"Step 363: Click on item item2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item2'))

	"Step 364: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 365: Click on button submitDynamicId10"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId10'))

	"Step 366: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 367: Click on button submitDynamicId10"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId10'))

	"Step 368: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 369: Click on button submitDynamicId10"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId10'))

	"Step 370: Click on link challengingForm -> Navigate to page '/challenging-form'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/link_challengingForm'))

	"Step 371: Enter input value in input enterFullName2"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_enterFullName2'), input_enterFullName2)

	"Step 372: Enter input value in input email7"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_email7'), input_email7)

	"Step 373: Enter input value in input phoneNumber6"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_phoneNumber6'), input_phoneNumber6)

	"Step 374: Click on span selectCountry8"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectCountry8'))

	"Step 375: Click on p singapore7"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_singapore7'))

	"Step 376: Click on span selectExperienceLevel8"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectExperienceLevel8'))

	"Step 377: Click on p expertExperience6"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_expertExperience6'))

	"Step 378: Click on span selectProgrammingLanguage8"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectProgrammingLanguage8'))

	"Step 379: Click on p java6"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_java6'))

	"Step 380: Fill out various native HTML date input fields and navigate pages"

	interactWithNativeHtmlElements.execute(input_basicDate_6, input_dateInput_6, input_dateInputWithRangeRestriction_6, input_requiredDate_6)

	"Step 381: Login into Application"

	TrueTestScripts.login()

	"Step 382: Navigate to /forms"

	TrueTestScripts.navigate("forms")

	"Step 383: Complete the MSU simulation form with personal and address details"

	fillMsuSimulationForm.execute(input_addressLine1_9, input_dateOfBirth_9, input_legalFirstName_9, input_legalLastName_9, input_phone_9, input_zipCode_9)

	"Step 384: Click on link sauceLogin -> Navigate to page '/sauce-login'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_msu_simulation_form/link_sauceLogin'))

	"Step 385: Login into Application"

	TrueTestScripts.login()

	"Step 386: Navigate to /sauce-login"

	TrueTestScripts.navigate("sauce-login")

	"Step 387: Click on button addToCart"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart'))

	"Step 388: Click on button addToCart2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart2'))

	"Step 389: Click on button addToCart3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart3'))

	"Step 390: Click on button remove"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_remove'))

	"Step 391: Add user roles and sort table data by headers"

	addUserRolesAndSortTableData.execute(select_status_9, select_userRole_10)

	"Step 392: Interact with AgGrid and perform product searches with filters"

	navigateAgGridAndSearchProducts.execute(input_hiddenValue_9, input_mandatoryField_9, input_optionalField_9, input_searchProducts_9, input_uncontrolledField_9)

	"Step 393: Fill out form inputs and submit with tracking enabled"

	fillFormInputsAndSubmitWithTracking.execute(input_emailAddress_9)

	"Step 394: Enter numbers in a list card and submit the data"

	enterNumbersInListCardAndSubmit.execute(input_enterNumber_9, input_enterNumber2_9)

	"Step 395: Interact with shadow DOM elements and select options"

	interactWithShadowDomAndSelectOptions.execute(select_formType_9, select_siteSelection_9)

	"Step 396: Input data and manage user profile with custom functions and options"

	manageUserProfileAndCustomFunctions.execute(input_doubleQuote_9, input_functionInput_9, input_scoreGreaterThan_9, select_options_9)

	"Step 397: Hover over button nightmareElement"

	WebUI.mouseOver(findTestObject('AI-Generated/Production/Page_xpath_breaking/button_nightmareElement'))

	"Step 398: Enter input value in input xmlNamespace"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_xpath_breaking/input_xmlNamespace'), input_xmlNamespace_15)

	"Step 399: Select option with input value from select dropdown"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_xpath_breaking/select_dropdown'), select_dropdown_15, "label", false)

	"Step 400: Click on link dynamicIdLocator -> Navigate to page '/dynamic-id-locator'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_xpath_breaking/link_dynamicIdLocator'))

	"Step 401: Select option with input value from select userRole"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/select_userRole'), select_userRole_30, "label", false)

	"Step 402: Enter input value in input email"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_email'), input_email_15)

	"Step 403: Enter input value in input enterText6"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_enterText6'), input_enterText6)

	"Step 404: Click on item item17"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item17'))

	"Step 405: Click on item item2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item2'))

	"Step 406: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 407: Click on button submitDynamicId11"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId11'))

	"Step 408: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 409: Click on button submitDynamicId11"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId11'))

	"Step 410: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 411: Click on button submitDynamicId11"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId11'))

	"Step 412: Click on link challengingForm -> Navigate to page '/challenging-form'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/link_challengingForm'))

	"Step 413: Enter input value in input enterFullName3"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_enterFullName3'), input_enterFullName3)

	"Step 414: Enter input value in input email8"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_email8'), input_email8)

	"Step 415: Enter input value in input phoneNumber7"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_phoneNumber7'), input_phoneNumber7)

	"Step 416: Click on span selectCountry9"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectCountry9'))

	"Step 417: Click on p singapore8"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_singapore8'))

	"Step 418: Click on span selectExperienceLevel9"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectExperienceLevel9'))

	"Step 419: Click on p expertOption3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_expertOption3'))

	"Step 420: Click on span selectProgrammingLanguage9"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectProgrammingLanguage9'))

	"Step 421: Click on p java7 -> Navigate to page '/native-element'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_java7'))

	"Step 422: Enter input value in input basicDate"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_basicDate'), input_basicDate_13)

	"Step 423: Enter input value in input dateInput"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_dateInput'), input_dateInput_13)

	"Step 424: Enter input value in input dateInputWithRangeRestriction"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_dateInputWithRangeRestriction'), input_dateInputWithRangeRestriction_13)

	"Step 425: Enter input value in input requiredDate"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_requiredDate'), input_requiredDate_13)

	"Step 426: Click on link about -> Navigate to page '/about'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_native_element/link_about'))

	"Step 427: Click on link navigateForms -> Navigate to page '/forms'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_about/link_forms'))

	"Step 428: Login into Application"

	TrueTestScripts.login()

	"Step 429: Navigate to /forms"

	TrueTestScripts.navigate("forms")

	"Step 430: Complete the MSU simulation form with personal and address details"

	fillMsuSimulationForm.execute(input_addressLine1_10, input_dateOfBirth_10, input_legalFirstName_10, input_legalLastName_10, input_phone_10, input_zipCode_10)

	"Step 431: Hover over button citizenshipStatus"

	WebUI.mouseOver(findTestObject('AI-Generated/Production/Page_msu_simulation_form/button_citizenshipStatus'))

	"Step 432: Click on link sauceLogin -> Navigate to page '/sauce-login'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_msu_simulation_form/link_sauceLogin'))

	"Step 433: Login into Application"

	TrueTestScripts.login()

	"Step 434: Navigate to /sauce-login"

	TrueTestScripts.navigate("sauce-login")

	"Step 435: Click on button addToCart"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart'))

	"Step 436: Click on button addToCart2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart2'))

	"Step 437: Click on button addToCart3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart3'))

	"Step 438: Click on button remove"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_remove'))

	"Step 439: Add user roles and sort table data by headers"

	addUserRolesAndSortTableData.execute(select_status_10, select_userRole_11)

	"Step 440: Interact with AgGrid and perform product searches with filters"

	navigateAgGridAndSearchProducts.execute(input_hiddenValue_10, input_mandatoryField_10, input_optionalField_10, input_searchProducts_10, input_uncontrolledField_10)

	"Step 441: Fill out form inputs and submit with tracking enabled"

	fillFormInputsAndSubmitWithTracking.execute(input_emailAddress_10)

	"Step 442: Enter numbers in a list card and submit the data"

	enterNumbersInListCardAndSubmit.execute(input_enterNumber_10, input_enterNumber2_10)

	"Step 443: Interact with shadow DOM elements and select options"

	interactWithShadowDomAndSelectOptions.execute(select_formType_10, select_siteSelection_10)

	"Step 444: Input data and manage user profile with custom functions and options"

	manageUserProfileAndCustomFunctions.execute(input_doubleQuote_10, input_functionInput_10, input_scoreGreaterThan_10, select_options_10)

	"Step 445: Enter input value in input xmlNamespace"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_xpath_breaking/input_xmlNamespace'), input_xmlNamespace_2)

	"Step 446: Select option with input value from select dropdown"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_xpath_breaking/select_dropdown'), select_dropdown_2, "label", false)

	"Step 447: Click on link dynamicIdLocator -> Navigate to page '/dynamic-id-locator'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_xpath_breaking/link_dynamicIdLocator'))

	"Step 448: Select option with input value from select userRole"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/select_userRole'), select_userRole_31, "label", false)

	"Step 449: Enter input value in input email"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_email'), input_email_2)

	"Step 450: Enter input value in input text3"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_text3'), input_text3)

	"Step 451: Click on item selectItem3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_selectItem3'))

	"Step 452: Click on item item2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item2'))

	"Step 453: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 454: Click on button submitDynamicId12"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId12'))

	"Step 455: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 456: Click on button submitDynamicId13"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId13'))

	"Step 457: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 458: Click on button submitDynamicId13"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId13'))

	"Step 459: Click on link challengingForm -> Navigate to page '/challenging-form'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/link_challengingForm'))

	"Step 460: Enter input value in input enterFullName4"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_enterFullName4'), input_enterFullName4)

	"Step 461: Enter input value in input email9"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_email9'), input_email9)

	"Step 462: Enter input value in input enterPhoneNumber3"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_enterPhoneNumber3'), input_enterPhoneNumber3)

	"Step 463: Click on span selectCountry10"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectCountry10'))

	"Step 464: Click on p singapore9"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_singapore9'))

	"Step 465: Click on span selectExperienceLevel10"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectExperienceLevel10'))

	"Step 466: Click on p expertExperience7"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_expertExperience7'))

	"Step 467: Click on span selectProgrammingLanguage10"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectProgrammingLanguage10'))

	"Step 468: Click on p java8"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_java8'))

	"Step 469: Fill out various native HTML date input fields and navigate pages"

	interactWithNativeHtmlElements.execute(input_basicDate_7, input_dateInput_7, input_dateInputWithRangeRestriction_7, input_requiredDate_7)

	"Step 470: Login into Application"

	TrueTestScripts.login()

	"Step 471: Navigate to /forms"

	TrueTestScripts.navigate("forms")

	"Step 472: Complete the MSU simulation form with personal and address details"

	fillMsuSimulationForm.execute(input_addressLine1_11, input_dateOfBirth_11, input_legalFirstName_11, input_legalLastName_11, input_phone_11, input_zipCode_11)

	"Step 473: Hover over button citizenshipStatus"

	WebUI.mouseOver(findTestObject('AI-Generated/Production/Page_msu_simulation_form/button_citizenshipStatus'))

	"Step 474: Click on link sauceLogin -> Navigate to page '/sauce-login'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_msu_simulation_form/link_sauceLogin'))

	"Step 475: Login into Application"

	TrueTestScripts.login()

	"Step 476: Navigate to /sauce-login"

	TrueTestScripts.navigate("sauce-login")

	"Step 477: Click on button addToCart"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart'))

	"Step 478: Click on button addToCart2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart2'))

	"Step 479: Click on button addToCart3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart3'))

	"Step 480: Click on button remove"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_remove'))

	"Step 481: Add user roles and sort table data by headers"

	addUserRolesAndSortTableData.execute(select_status_11, select_userRole_12)

	"Step 482: Interact with AgGrid and perform product searches with filters"

	navigateAgGridAndSearchProducts.execute(input_hiddenValue_11, input_mandatoryField_11, input_optionalField_11, input_searchProducts_11, input_uncontrolledField_11)

	"Step 483: Fill out form inputs and submit with tracking enabled"

	fillFormInputsAndSubmitWithTracking.execute(input_emailAddress_11)

	"Step 484: Enter numbers in a list card and submit the data"

	enterNumbersInListCardAndSubmit.execute(input_enterNumber_11, input_enterNumber2_11)

	"Step 485: Interact with shadow DOM elements and select options"

	interactWithShadowDomAndSelectOptions.execute(select_formType_11, select_siteSelection_11)

	"Step 486: Input data and manage user profile with custom functions and options"

	manageUserProfileAndCustomFunctions.execute(input_doubleQuote_11, input_functionInput_11, input_scoreGreaterThan_11, select_options_11)

	"Step 487: Hover over button nightmareElement"

	WebUI.mouseOver(findTestObject('AI-Generated/Production/Page_xpath_breaking/button_nightmareElement'))

	"Step 488: Enter input value in input xmlNamespace"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_xpath_breaking/input_xmlNamespace'), input_xmlNamespace_3)

	"Step 489: Select option with input value from select dropdown"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_xpath_breaking/select_dropdown'), select_dropdown_3, "label", false)

	"Step 490: Click on link dynamicIdLocator -> Navigate to page '/dynamic-id-locator'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_xpath_breaking/link_dynamicIdLocator'))

	"Step 491: Select option with input value from select userRole"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/select_userRole'), select_userRole_19, "label", false)

	"Step 492: Enter input value in input email"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_email'), input_email_3)

	"Step 493: Enter input value in input enterText7"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_enterText7'), input_enterText7)

	"Step 494: Click on item item18"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item18'))

	"Step 495: Click on item item2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item2'))

	"Step 496: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 497: Click on button submitDynamicId14"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId14'))

	"Step 498: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 499: Click on button submitDynamicId14"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId14'))

	"Step 500: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 501: Click on button submitDynamicId14"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId14'))

	"Step 502: Click on link challengingForm -> Navigate to page '/challenging-form'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/link_challengingForm'))

	"Step 503: Enter input value in input enterFullName5"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_enterFullName5'), input_enterFullName5)

	"Step 504: Enter input value in input email10"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_email10'), input_email10)

	"Step 505: Enter input value in input phoneNumber8"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_phoneNumber8'), input_phoneNumber8)

	"Step 506: Click on span selectCountry11"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectCountry11'))

	"Step 507: Click on p singapore10"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_singapore10'))

	"Step 508: Click on span selectExperienceLevel11"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectExperienceLevel11'))

	"Step 509: Click on p expertExperience8"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_expertExperience8'))

	"Step 510: Click on span selectProgrammingLanguage11"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectProgrammingLanguage11'))

	"Step 511: Click on p java9 -> Navigate to page '/native-element'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_java9'))

	"Step 512: Enter input value in input basicDate"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_basicDate'), input_basicDate_14)

	"Step 513: Enter input value in input dateInput"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_dateInput'), input_dateInput_14)

	"Step 514: Enter input value in input dateInputWithRangeRestriction"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_dateInputWithRangeRestriction'), input_dateInputWithRangeRestriction_14)

	"Step 515: Enter input value in input requiredDate"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_requiredDate'), input_requiredDate_14)

	"Step 516: Click on link about -> Navigate to page '/about'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_native_element/link_about'))

	"Step 517: Click on link navigateForms -> Navigate to page '/forms'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_about/link_forms'))

	"Step 518: Login into Application"

	TrueTestScripts.login()

	"Step 519: Navigate to /forms"

	TrueTestScripts.navigate("forms")

	"Step 520: Complete the MSU simulation form with personal and address details"

	fillMsuSimulationForm.execute(input_addressLine1_12, input_dateOfBirth_12, input_legalFirstName_12, input_legalLastName_12, input_phone_12, input_zipCode_12)

	"Step 521: Hover over button citizenshipStatus"

	WebUI.mouseOver(findTestObject('AI-Generated/Production/Page_msu_simulation_form/button_citizenshipStatus'))

	"Step 522: Click on link sauceLogin -> Navigate to page '/sauce-login'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_msu_simulation_form/link_sauceLogin'))

	"Step 523: Login into Application"

	TrueTestScripts.login()

	"Step 524: Navigate to /sauce-login"

	TrueTestScripts.navigate("sauce-login")

	"Step 525: Click on button addToCart"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart'))

	"Step 526: Click on button addToCart2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart2'))

	"Step 527: Click on button addToCart3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart3'))

	"Step 528: Click on button remove"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_remove'))

	"Step 529: Add user roles and sort table data by headers"

	addUserRolesAndSortTableData.execute(select_status_12, select_userRole_13)

	"Step 530: Interact with AgGrid and perform product searches with filters"

	navigateAgGridAndSearchProducts.execute(input_hiddenValue_12, input_mandatoryField_12, input_optionalField_12, input_searchProducts_12, input_uncontrolledField_12)

	"Step 531: Fill out form inputs and submit with tracking enabled"

	fillFormInputsAndSubmitWithTracking.execute(input_emailAddress_12)

	"Step 532: Enter numbers in a list card and submit the data"

	enterNumbersInListCardAndSubmit.execute(input_enterNumber_12, input_enterNumber2_12)

	"Step 533: Interact with shadow DOM elements and select options"

	interactWithShadowDomAndSelectOptions.execute(select_formType_12, select_siteSelection_12)

	"Step 534: Input data and manage user profile with custom functions and options"

	manageUserProfileAndCustomFunctions.execute(input_doubleQuote_12, input_functionInput_12, input_scoreGreaterThan_12, select_options_12)

	"Step 535: Enter input value in input xmlNamespace"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_xpath_breaking/input_xmlNamespace'), input_xmlNamespace_4)

	"Step 536: Select option with input value from select dropdown"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_xpath_breaking/select_dropdown'), select_dropdown_4, "label", false)

	"Step 537: Click on link dynamicIdLocator -> Navigate to page '/dynamic-id-locator'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_xpath_breaking/link_dynamicIdLocator'))

	"Step 538: Select option with input value from select userRole"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/select_userRole'), select_userRole_20, "label", false)

	"Step 539: Enter input value in input email"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_email'), input_email_4)

	"Step 540: Enter input value in input enterText8"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_enterText8'), input_enterText8)

	"Step 541: Click on item item19"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item19'))

	"Step 542: Click on item item2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item2'))

	"Step 543: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 544: Click on button submitDynamicId15"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId15'))

	"Step 545: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 546: Click on button submitDynamicId15"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId15'))

	"Step 547: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 548: Click on button submitDynamicId15"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId15'))

	"Step 549: Click on link challengingForm -> Navigate to page '/challenging-form'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/link_challengingForm'))

	"Step 550: Enter input value in input fullName7"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_fullName7'), input_fullName7)

	"Step 551: Enter input value in input enterEmail2"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_enterEmail2'), input_enterEmail2)

	"Step 552: Enter input value in input enterPhoneNumber4"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_enterPhoneNumber4'), input_enterPhoneNumber4)

	"Step 553: Click on span selectCountry12"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectCountry12'))

	"Step 554: Click on p selectSingapore2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_selectSingapore2'))

	"Step 555: Click on span selectExperienceLevel12"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectExperienceLevel12'))

	"Step 556: Click on p expertExperience9"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_expertExperience9'))

	"Step 557: Click on span selectProgrammingLanguage12"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectProgrammingLanguage12'))

	"Step 558: Click on p java10"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_java10'))

	"Step 559: Fill out various native HTML date input fields and navigate pages"

	interactWithNativeHtmlElements.execute(input_basicDate_8, input_dateInput_8, input_dateInputWithRangeRestriction_8, input_requiredDate_8)

	"Step 560: Login into Application"

	TrueTestScripts.login()

	"Step 561: Navigate to /forms"

	TrueTestScripts.navigate("forms")

	"Step 562: Complete the MSU simulation form with personal and address details"

	fillMsuSimulationForm.execute(input_addressLine1_13, input_dateOfBirth_13, input_legalFirstName_13, input_legalLastName_13, input_phone_13, input_zipCode_13)

	"Step 563: Click on link sauceLogin -> Navigate to page '/sauce-login'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_msu_simulation_form/link_sauceLogin'))

	"Step 564: Login into Application"

	TrueTestScripts.login()

	"Step 565: Navigate to /sauce-login"

	TrueTestScripts.navigate("sauce-login")

	"Step 566: Click on button addToCart"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart'))

	"Step 567: Click on button addToCart2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart2'))

	"Step 568: Click on button addToCart3"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart3'))

	"Step 569: Click on button remove"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_remove'))

	"Step 570: Add user roles and sort table data by headers"

	addUserRolesAndSortTableData.execute(select_status_13, select_userRole_14)

	"Step 571: Interact with AgGrid and perform product searches with filters"

	navigateAgGridAndSearchProducts.execute(input_hiddenValue_13, input_mandatoryField_13, input_optionalField_13, input_searchProducts_13, input_uncontrolledField_13)

	"Step 572: Fill out form inputs and submit with tracking enabled"

	fillFormInputsAndSubmitWithTracking.execute(input_emailAddress_13)

	"Step 573: Enter numbers in a list card and submit the data"

	enterNumbersInListCardAndSubmit.execute(input_enterNumber_13, input_enterNumber2_13)

	"Step 574: Interact with shadow DOM elements and select options"

	interactWithShadowDomAndSelectOptions.execute(select_formType_13, select_siteSelection_13)

	"Step 575: Input data and manage user profile with custom functions and options"

	manageUserProfileAndCustomFunctions.execute(input_doubleQuote_13, input_functionInput_13, input_scoreGreaterThan_13, select_options_13)

	"Step 576: Enter input value in input xmlNamespace"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_xpath_breaking/input_xmlNamespace'), input_xmlNamespace_5)

	"Step 577: Select option with input value from select dropdown"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_xpath_breaking/select_dropdown'), select_dropdown_5, "label", false)

	"Step 578: Click on link dynamicIdLocator -> Navigate to page '/dynamic-id-locator'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_xpath_breaking/link_dynamicIdLocator'))

	"Step 579: Select option with input value from select userRole"

	TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/select_userRole'), select_userRole_21, "label", false)

	"Step 580: Enter input value in input email"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_email'), input_email_5)

	"Step 581: Enter input value in input enterText9"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_enterText9'), input_enterText9)

	"Step 582: Click on item item110"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item110'))

	"Step 583: Click on item item2"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item2'))

	"Step 584: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 585: Click on button submitDynamicId16"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId16'))

	"Step 586: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 587: Click on button submitDynamicId16"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId16'))

	"Step 588: Click on button submitForm"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

	"Step 589: Click on button submitDynamicId16"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId16'))

	"Step 590: Click on link challengingForm -> Navigate to page '/challenging-form'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/link_challengingForm'))

	"Step 591: Enter input value in input fullName8"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_fullName8'), input_fullName8)

	"Step 592: Enter input value in input email11"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_email11'), input_email11)

	"Step 593: Enter input value in input phoneNumber9"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_phoneNumber9'), input_phoneNumber9)

	"Step 594: Click on span selectCountry13"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectCountry13'))

	"Step 595: Click on p singapore11"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_singapore11'))

	"Step 596: Click on span selectExperienceLevel13"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectExperienceLevel13'))

	"Step 597: Click on p expertOption4"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_expertOption4'))

	"Step 598: Click on span selectProgrammingLanguage13"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectProgrammingLanguage13'))

	"Step 599: Click on p java11 -> Navigate to page '/native-element'"

	WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_java11'))

	"Step 600: Enter input value in input basicDate"

	WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_basicDate'), input_basicDate_15)

}

part1
part2
"Step 601: Enter input value in input dateInput"

WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_dateInput'), input_dateInput_15)

"Step 602: Enter input value in input dateInputWithRangeRestriction"

WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_dateInputWithRangeRestriction'), input_dateInputWithRangeRestriction_15)

"Step 603: Enter input value in input requiredDate"

WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_requiredDate'), input_requiredDate_15)

"Step 604: Click on link about -> Navigate to page '/about'"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_native_element/link_about'))

"Step 605: Click on link navigateForms -> Navigate to page '/forms'"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_about/link_forms'))

"Step 606: Login into Application"

TrueTestScripts.login()

"Step 607: Navigate to /forms"

TrueTestScripts.navigate("forms")

"Step 608: Complete the MSU simulation form with personal and address details"

fillMsuSimulationForm.execute(input_addressLine1_14, input_dateOfBirth_14, input_legalFirstName_14, input_legalLastName_14, input_phone_14, input_zipCode_14)

"Step 609: Click on link sauceLogin -> Navigate to page '/sauce-login'"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_msu_simulation_form/link_sauceLogin'))

"Step 610: Login into Application"

TrueTestScripts.login()

"Step 611: Navigate to /sauce-login"

TrueTestScripts.navigate("sauce-login")

"Step 612: Click on button addToCart"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart'))

"Step 613: Click on button addToCart2"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart2'))

"Step 614: Click on button addToCart3"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart3'))

"Step 615: Click on button remove"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_remove'))

"Step 616: Add user roles and sort table data by headers"

addUserRolesAndSortTableData.execute(select_status_14, select_userRole_15)

"Step 617: Interact with AgGrid and perform product searches with filters"

navigateAgGridAndSearchProducts.execute(input_hiddenValue_14, input_mandatoryField_14, input_optionalField_14, input_searchProducts_14, input_uncontrolledField_14)

"Step 618: Fill out form inputs and submit with tracking enabled"

fillFormInputsAndSubmitWithTracking.execute(input_emailAddress_14)

"Step 619: Enter numbers in a list card and submit the data"

enterNumbersInListCardAndSubmit.execute(input_enterNumber_14, input_enterNumber2_14)

"Step 620: Interact with shadow DOM elements and select options"

interactWithShadowDomAndSelectOptions.execute(select_formType_14, select_siteSelection_14)

"Step 621: Input data and manage user profile with custom functions and options"

manageUserProfileAndCustomFunctions.execute(input_doubleQuote_14, input_functionInput_14, input_scoreGreaterThan_14, select_options_14)

"Step 622: Enter input value in input xmlNamespace"

WebUI.setText(findTestObject('AI-Generated/Production/Page_xpath_breaking/input_xmlNamespace'), input_xmlNamespace_6)

"Step 623: Select option with input value from select dropdown"

TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_xpath_breaking/select_dropdown'), select_dropdown_6, "label", false)

"Step 624: Click on link dynamicIdLocator -> Navigate to page '/dynamic-id-locator'"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_xpath_breaking/link_dynamicIdLocator'))

"Step 625: Select option with input value from select userRole"

TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/select_userRole'), select_userRole_22, "label", false)

"Step 626: Enter input value in input email"

WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_email'), input_email_6)

"Step 627: Enter input value in input enterText10"

WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_enterText10'), input_enterText10)

"Step 628: Click on item item111"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item111'))

"Step 629: Click on item item2"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item2'))

"Step 630: Click on button submitForm"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

"Step 631: Click on button submitDynamicId17"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId17'))

"Step 632: Click on button submitForm"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

"Step 633: Click on button submitDynamicId17"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId17'))

"Step 634: Click on button submitForm"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

"Step 635: Click on button submitDynamicId17"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId17'))

"Step 636: Click on link challengingForm -> Navigate to page '/challenging-form'"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/link_challengingForm'))

"Step 637: Enter input value in input fullName9"

WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_fullName9'), input_fullName9)

"Step 638: Enter input value in input email12"

WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_email12'), input_email12)

"Step 639: Enter input value in input enterPhoneNumber5"

WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_enterPhoneNumber5'), input_enterPhoneNumber5)

"Step 640: Click on span selectCountry14"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectCountry14'))

"Step 641: Click on p singapore12"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_singapore12'))

"Step 642: Click on span selectExperienceLevel14"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectExperienceLevel14'))

"Step 643: Click on p expertExperience10"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_expertExperience10'))

"Step 644: Click on span selectProgrammingLanguage14"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectProgrammingLanguage14'))

"Step 645: Click on p java12"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_java12'))

"Step 646: Fill out various native HTML date input fields and navigate pages"

interactWithNativeHtmlElements.execute(input_basicDate_9, input_dateInput_9, input_dateInputWithRangeRestriction_9, input_requiredDate_9)

"Step 647: Login into Application"

TrueTestScripts.login()

"Step 648: Navigate to /forms"

TrueTestScripts.navigate("forms")

"Step 649: Complete the MSU simulation form with personal and address details"

fillMsuSimulationForm.execute(input_addressLine1_15, input_dateOfBirth_15, input_legalFirstName_15, input_legalLastName_15, input_phone_15, input_zipCode_15)

"Step 650: Hover over button legalSuffix"

WebUI.mouseOver(findTestObject('AI-Generated/Production/Page_msu_simulation_form/button_legalSuffix'))

"Step 651: Click on link sauceLogin -> Navigate to page '/sauce-login'"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_msu_simulation_form/link_sauceLogin'))

"Step 652: Login into Application"

TrueTestScripts.login()

"Step 653: Navigate to /sauce-login"

TrueTestScripts.navigate("sauce-login")

"Step 654: Click on button addToCart"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart'))

"Step 655: Click on button addToCart2"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart2'))

"Step 656: Click on button addToCart3"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart3'))

"Step 657: Click on button remove"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_remove'))

"Step 658: Add user roles and sort table data by headers"

addUserRolesAndSortTableData.execute(select_status_15, select_userRole_16)

"Step 659: Interact with AgGrid and perform product searches with filters"

navigateAgGridAndSearchProducts.execute(input_hiddenValue_15, input_mandatoryField_15, input_optionalField_15, input_searchProducts_15, input_uncontrolledField_15)

"Step 660: Fill out form inputs and submit with tracking enabled"

fillFormInputsAndSubmitWithTracking.execute(input_emailAddress_15)

"Step 661: Enter numbers in a list card and submit the data"

enterNumbersInListCardAndSubmit.execute(input_enterNumber_15, input_enterNumber2_15)

"Step 662: Interact with shadow DOM elements and select options"

interactWithShadowDomAndSelectOptions.execute(select_formType_15, select_siteSelection_15)

"Step 663: Input data and manage user profile with custom functions and options"

manageUserProfileAndCustomFunctions.execute(input_doubleQuote_15, input_functionInput_15, input_scoreGreaterThan_15, select_options_15)

"Step 664: Enter input value in input xmlNamespace"

WebUI.setText(findTestObject('AI-Generated/Production/Page_xpath_breaking/input_xmlNamespace'), input_xmlNamespace_7)

"Step 665: Select option with input value from select dropdown"

TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_xpath_breaking/select_dropdown'), select_dropdown_7, "label", false)

"Step 666: Click on link dynamicIdLocator -> Navigate to page '/dynamic-id-locator'"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_xpath_breaking/link_dynamicIdLocator'))

"Step 667: Select option with input value from select userRole"

TrueTestScripts.selectOption(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/select_userRole'), select_userRole_23, "label", false)

"Step 668: Enter input value in input email"

WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_email'), input_email_7)

"Step 669: Enter input value in input enterText11"

WebUI.setText(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/input_enterText11'), input_enterText11)

"Step 670: Click on item item112"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item112'))

"Step 671: Click on item item2"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/item_item2'))

"Step 672: Click on button submitForm"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

"Step 673: Click on button submitDynamicId18"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId18'))

"Step 674: Click on button submitForm"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

"Step 675: Click on button submitDynamicId18"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId18'))

"Step 676: Click on button submitForm"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitForm'))

"Step 677: Click on button submitDynamicId19"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/button_submitDynamicId19'))

"Step 678: Click on link challengingForm -> Navigate to page '/challenging-form'"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_dynamic_id_locator/link_challengingForm'))

"Step 679: Enter input value in input enterFullName6"

WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_enterFullName6'), input_enterFullName6)

"Step 680: Enter input value in input email13"

WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_email13'), input_email13)

"Step 681: Enter input value in input phoneNumber10"

WebUI.setText(findTestObject('AI-Generated/Production/Page_challenging_form/input_phoneNumber10'), input_phoneNumber10)

"Step 682: Click on span selectCountry15"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectCountry15'))

"Step 683: Click on p singapore13"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_singapore13'))

"Step 684: Click on span selectExperienceLevel15"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectExperienceLevel15'))

"Step 685: Click on p expertExperience11"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_expertExperience11'))

"Step 686: Click on span selectProgrammingLanguage15"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/span_selectProgrammingLanguage15'))

"Step 687: Click on p java13 -> Navigate to page '/native-element'"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_challenging_form/p_java13'))

"Step 688: Enter input value in input basicDate"

WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_basicDate'), input_basicDate_16)

"Step 689: Enter input value in input dateInput"

WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_dateInput'), input_dateInput_16)

"Step 690: Enter input value in input dateInputWithRangeRestriction"

WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_dateInputWithRangeRestriction'), input_dateInputWithRangeRestriction_16)

"Step 691: Enter input value in input requiredDate"

WebUI.setText(findTestObject('AI-Generated/Production/Page_native_element/input_requiredDate'), input_requiredDate_16)

"Step 692: Click on link about -> Navigate to page '/about'"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_native_element/link_about'))

"Step 693: Click on link navigateForms -> Navigate to page '/forms'"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_about/link_forms'))

"Step 694: Login into Application"

TrueTestScripts.login()

"Step 695: Navigate to /forms"

TrueTestScripts.navigate("forms")

"Step 696: Complete the MSU simulation form with personal and address details"

fillMsuSimulationForm.execute(input_addressLine1_16, input_dateOfBirth_16, input_legalFirstName_16, input_legalLastName_16, input_phone_16, input_zipCode_16)

"Step 697: Hover over button citizenshipStatus"

WebUI.mouseOver(findTestObject('AI-Generated/Production/Page_msu_simulation_form/button_citizenshipStatus'))

"Step 698: Click on link sauceLogin -> Navigate to page '/sauce-login'"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_msu_simulation_form/link_sauceLogin'))

"Step 699: Login into Application"

TrueTestScripts.login()

"Step 700: Navigate to /sauce-login"

TrueTestScripts.navigate("sauce-login")

"Step 701: Click on button addToCart"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart'))

"Step 702: Click on button addToCart2"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart2'))

"Step 703: Click on button addToCart3"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_addToCart3'))

"Step 704: Click on button remove"

WebUI.enhancedClick(findTestObject('AI-Generated/Production/Page_sauce_login/button_remove'))

"Step 705: Add user roles and sort table data by headers"

addUserRolesAndSortTableData.execute(select_status_16, select_userRole_17)

"Step 706: Interact with AgGrid and perform product searches with filters"

navigateAgGridAndSearchProducts.execute(input_hiddenValue_16, input_mandatoryField_16, input_optionalField_16, input_searchProducts_16, input_uncontrolledField_16)

"Step 707: Fill out form inputs and submit with tracking enabled"

fillFormInputsAndSubmitWithTracking.execute(input_emailAddress_16)

"Step 708: Enter numbers in a list card and submit the data"

enterNumbersInListCardAndSubmit.execute(input_enterNumber_16, input_enterNumber2_16)

"Step 709: Interact with shadow DOM elements and select options"

interactWithShadowDomAndSelectOptions.execute(select_formType_16, select_siteSelection_16)

"Step 710: Take full page screenshot as checkpoint"

WebUI.takeFullPageScreenshotAsCheckpoint('TC1-Comprehensive User Interaction and Form Submission Workflow_visual_checkpoint')


'Terminate test session: Close browser'

@com.kms.katalon.core.annotation.TearDown
def teardown() {
	WebUI.closeBrowser()
}
