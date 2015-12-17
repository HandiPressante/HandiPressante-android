package com.handipressante.handipressante;

public class Sheet {

    public Integer _id;
    public String _name;
    public String _description;
    public Integer _rankGeneral;
    public Integer _rankCleanliness;
    public Integer _rankFacilities;
    public Integer _rankAccessibility;
    public Boolean _isAdapted;

    public Sheet(){
        _id = -1;
        _name = "Undefined";
        _description = "Undefined";
        _rankGeneral = -1;
        _rankCleanliness = -1;
        _rankFacilities = -1;
        _rankAccessibility = -1;
        _isAdapted = false;
    }

    public Sheet(Integer id,String name, String description,Integer rankGeneral,Integer rankCleanliness,Integer rankFacilities,Integer rankAccessibility, Boolean isAdapted){
        _id = id;
        _name = name;
        _description = description;
        _rankGeneral = rankGeneral;
        _rankCleanliness = rankCleanliness;
        _rankFacilities = rankFacilities;
        _rankAccessibility = rankAccessibility;
        _isAdapted = isAdapted;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_description() {
        return _description;
    }

    public void set_description(String _description) {
        this._description = _description;
    }

    public Integer get_rankGeneral() {
        return _rankGeneral;
    }

    public void set_rankGeneral(Integer _rankGeneral) {
        this._rankGeneral = _rankGeneral;
    }

    public Integer get_rankCleanliness() {
        return _rankCleanliness;
    }

    public void set_rankCleanliness(Integer _rankCleanliness) {
        this._rankCleanliness = _rankCleanliness;
    }

    public Integer get_rankFacilities() {
        return _rankFacilities;
    }

    public void set_rankFacilities(Integer _rankFacilities) {
        this._rankFacilities = _rankFacilities;
    }

    public Integer get_rankAccessibility() {
        return _rankAccessibility;
    }

    public void set_rankAccessibility(Integer _rankAccessibility) {
        this._rankAccessibility = _rankAccessibility;
    }

    public Boolean get_isAdapted() {
        return _isAdapted;
    }

    public void set_isAdapted(Boolean _isAdapted) {
        this._isAdapted = _isAdapted;
    }
}