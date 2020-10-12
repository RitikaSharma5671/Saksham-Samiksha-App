package com.samagra.parent.helper;

import android.app.Activity;
import android.widget.Spinner;

import com.samagra.parent.UtilityFunctions;

import java.lang.reflect.Method;

// TODO : Add documentation
public class KeyboardHandler {
    public boolean isDropDownOpen;
    public boolean isUDISEKeyboardShowing;
    public Spinner spinner;
    Activity activity;

    public static String config = "[\n" +
            "  {\n" +
            "    \"base64Icon\": \"iVBORw0KGgoAAAANSUhEUgAAACQAAAAkCAYAAADhAJiYAAAA10lEQVR4Ae3OAQYCURCH8Q4QFiEsFh3gHSEAQkcJIUDoAHuADhBCCKEjhBAChBDgIUCYPrSr0pPRYGM+fsDfmJb3J3mel2OOA/YokRnuVRWIkDcnZOq9QStIQqneGxQhCXv13qArJOGo3hu0hSTM1XuDAm6QNxG5em9UwBZXRKxQ2Ozt8woMMcb0YYQBukjVT+/1BZQ4Qb44YoYenmtjrdh/rMAGAq0bFuigKsNFsX8pIEJ+dEaOqolyX7eDGFmiKij3dWIooipT7uvEmPZ2Ex9qfp7ned4d/D4cHBcteyYAAAAASUVORK5CYII=\",\n" +
            "    \"title\": \"Name\",\n" +
            "    \"content\": \"user.fullName\",\n" +
            "    \"isEditable\": true,\n" +
            "    \"section\": 0,\n" +
            "    \"type\": \"TEXT\",\n" +
            "    \"spinnerExtra\": null\n" +
            "  },\n" +
            "  {\n" +
            "    \"base64Icon\": \"iVBORw0KGgoAAAANSUhEUgAAABIAAAASCAYAAABWzo5XAAAAo0lEQVR4Ac3RAQbDMBiG4aIoih1i2CEG9Ag9QI5QQAEDDEOOEUCP8B+pKCD492L4qIy00JcHbfiINJfuCftJ6OXMCiJ2GVwEOfMCw64EF9PRoQgXy9GhAS7mo0MdNjgiNEfGBy8RmkIRGXdoGWNT0Q0rDNqI6gIcb5wuylgr/2csmBDQ42+tjBke8q1SzTVX5PLz1z1AxHZqSOowICLBMOKifQG8rFz+lUOMLwAAAABJRU5ErkJggg==\",\n" +
            "    \"title\": \"Contact Number - Please note this number will be used for sending OTP for password reset.\",\n" +
            "    \"content\": \"user.mobilePhone\",\n" +
            "    \"isEditable\": true,\n" +
            "    \"section\": 0,\n" +
            "    \"type\": \"PHONE_NUMBER\",\n" +
            "    \"spinnerExtra\": null\n" +
            "  },\n" +
            "  {\n" +
            "    \"base64Icon\": \"iVBORw0KGgoAAAANSUhEUgAAACQAAAAkCAYAAADhAJiYAAAA50lEQVR4Ae3TERACQRSH8eAwyCfIKcwlCIOcgjAI8zkMwjA4DPIJwoN8DvI5ODx4ffJmkjdzszv36H0zP23+3e5OoihyrsAZH8jIHtjAjvYQZweY1RBnNcwEao92hAFf7CBq2CCiOSpILvS4YAZKG6Rt0GQeywpa0qArZtCmKNFDBupwRAFtjnvKID3vLf5b4j3wWS+gFTgY99Is48cz/oRV4uevjEs7/JitMi7oGquMh2CU+oTJ+GpwGGTcF+Ne+Q5ST/vS+g/KFoNiUAxyYNZBnL1gdoM428FsihMahyEtShSIositH2RCe+5ek0fEAAAAAElFTkSuQmCC\",\n" +
            "    \"title\": \"Official Email\",\n" +
            "    \"content\": \"user.email\",\n" +
            "    \"isEditable\": true,\n" +
            "    \"section\": 0,\n" +
            "    \"type\": \"TEXT\",\n" +
            "    \"spinnerExtra\": null\n" +
            "  },\n" +
            "  {\n" +
            "    \"base64Icon\": \"iVBORw0KGgoAAAANSUhEUgAAACQAAAAkCAYAAADhAJiYAAAA10lEQVR4Ae3OAQYCURCH8Q4QFiEsFh3gHSEAQkcJIUDoAHuADhBCCKEjhBAChBDgIUCYPrSr0pPRYGM+fsDfmJb3J3mel2OOA/YokRnuVRWIkDcnZOq9QStIQqneGxQhCXv13qArJOGo3hu0hSTM1XuDAm6QNxG5em9UwBZXRKxQ2Ozt8woMMcb0YYQBukjVT+/1BZQ4Qb44YoYenmtjrdh/rMAGAq0bFuigKsNFsX8pIEJ+dEaOqolyX7eDGFmiKij3dWIooipT7uvEmPZ2Ex9qfp7ned4d/D4cHBcteyYAAAAASUVORK5CYII=\",\n" +
            "    \"title\": \"Username\",\n" +
            "    \"content\": \"user.username\",\n" +
            "    \"isEditable\": false,\n" +
            "    \"section\": 1,\n" +
            "    \"type\": \"TEXT\",\n" +
            "    \"spinnerExtra\": null\n" +
            "  },\n" +
            "  {\n" +
            "    \"base64Icon\": \"iVBORw0KGgoAAAANSUhEUgAAACQAAAAkCAYAAADhAJiYAAAB3ElEQVR4Ae3XAWRCURSA4eHiIQxhgCHAMAwDxDAMwxACDMMQHoYhhDCEYRhCCA/DAEMI4WEIQwghDA8hhLMfjefo6LpesPXzAanjdjtPB/+lfado4Q0ppmtDJHhABTstQgMziKcx6ii8G0wggT5xjkJqQQqwwi2Cc0ggBWsjqDZkF0JOqg7ZoRXO4NUh5hDDCHVcIMZ38EX3rAkxJHDIdxQ6lM9KiJBBDGVs6g6izNHDBMGndAUxfMHqRN2Peu4kHfoQQwVmrxDDAlaXkLW+NbAhhtkYAksNm0qMPePz3n0Ybb+cGa7UnXuC5EyhK2EBgTaAmXiaYQTrQ+7w2zESiGEKMynQGB9YQUIHyiCwfOMdXXTQxQgrSKAhzL4gygQPODbuRs/YQSMMYH+1QAKzntonMRw25fChjr4Ba6+cowM93CPMapC1FL6vfUYEnyrqKz6FWQlLyFoVVl0I5ojgW0Ndh6111LPGbRloAN/KatfdY2tHyOzNGzyQw7s6HQevYkhOo4CBniE5l/BP/5zRhgsYqIQ3SE4z9H/YEJKTogrfgWqYGQ/ToCJj8aUYQw90iCqamEKUFgopRgYJNMcNCq2MDpYQTxkeEWFnlXCNHj7VXpkhxQsu4PC32/cDRGLyw1kNeBMAAAAASUVORK5CYII=\",\n" +
            "    \"title\": \"Designation\",\n" +
            "    \"content\": \"user.designation\",\n" +
            "    \"isEditable\": false,\n" +
            "    \"section\": 1,\n" +
            "    \"type\": \"TEXT\",\n" +
            "    \"spinnerExtra\": null\n" +
            "  }\n" +
            "]";

    public KeyboardHandler(boolean isDropDownOpen, boolean isUDISEKeyboardShowing, Spinner spinner, Activity activity) {
        this.isDropDownOpen = isDropDownOpen;
        this.isUDISEKeyboardShowing = isUDISEKeyboardShowing;
        this.spinner = spinner;
        this.activity = activity;
    }

    public void closeDropDown() {
        // If DROPDOWN and UDISE clicked, close DROPDOWN
        if (this.isDropDownOpen) hideSpinnerDropDown();
        this.isDropDownOpen = false;
    }

    public void closeUDISEKeyboard() {
        // If UDISE and DROPDOWN clicked, close UDISE
        if (this.isUDISEKeyboardShowing) UtilityFunctions.hideKeyboard(this.activity);
        this.isUDISEKeyboardShowing = false;
    }

    public void hideSpinnerDropDown() {
        try {
            Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
            method.setAccessible(true);
            method.invoke(this.spinner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
