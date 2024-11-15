package com.ai.dixorai;

public class lang_basic_data {
    public String lang_common_name,lang_short_name;
    public String prompt_tag;
    public int flagResourceId;

    public lang_basic_data(String lang_common_name, String lang_short_name, int flagResourceId) {
        this.lang_common_name = lang_common_name;
        this.lang_short_name = lang_short_name;
        this.flagResourceId = flagResourceId;
    }

    public lang_basic_data(String lang_common_name, String lang_short_name) {
        this.lang_common_name = lang_common_name;
        this.lang_short_name = lang_short_name;
    }

    public String getLang_common_name() {
        return lang_common_name;
    }

    public void setLang_common_name(String lang_common_name) {
        this.lang_common_name = lang_common_name;
    }

    public String getLang_short_name() {
        return lang_short_name;
    }

    public void setPrompt_tag(String prompt_tag) {
        this.prompt_tag = prompt_tag;
    }

    public String getPrompt_tag() {
        return prompt_tag;
    }


    public lang_basic_data(String lang_common_name, String lang_short_name, String prompt_tag, int flagResourceId) {
        this.lang_common_name = lang_common_name;
        this.lang_short_name = lang_short_name;
        this.prompt_tag = prompt_tag;
        this.flagResourceId = flagResourceId;
    }

    public lang_basic_data(String lang_common_name, String lang_short_name, String prompt_tag) {
        this.lang_common_name = lang_common_name;
        this.lang_short_name = lang_short_name;
        this.prompt_tag = prompt_tag;
    }

    public int getFlagResourceId() {
        return flagResourceId;
    }

    public void setFlagResourceId(int flagResourceId) {
        this.flagResourceId = flagResourceId;
    }
}
