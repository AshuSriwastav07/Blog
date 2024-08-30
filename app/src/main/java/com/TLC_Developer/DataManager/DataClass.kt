package com.TLC_Developer.DataManager

class DataClass {

    var BlogTitle:String
    var BlogTags:String
    var BlogBody:String
    var BlogUserID:String  //email
    var BlogDateAndTime:String
    var BlogImageURL:String  //BG
    var BlogUserProfileUrl:String //Profile
    var BlogDocumentID:String //document id where it set up
    var BlogUserName:String

    constructor(blogTitle:String,blogTags:String,blogBody:String,blogUserID:String,blogDateAndTime:String,blogImageURL:String,blogUserProfileUrl: String,blogDocumentID:String,BlogUserName:String)
    {
        this.BlogTitle=blogTitle
        this.BlogTags=blogTags
        this.BlogBody=blogBody
        this.BlogUserID=blogUserID
        this.BlogDateAndTime=blogDateAndTime
        this.BlogImageURL=blogImageURL
        this.BlogUserProfileUrl=blogUserProfileUrl
        this.BlogDocumentID=blogDocumentID
        this.BlogUserName=BlogUserName

    }
}