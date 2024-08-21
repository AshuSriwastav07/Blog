package com.TLC_Developer.DataManager

import com.google.firebase.storage.StorageReference

class DataClass {

    var BlogTitle:String
    var BlogTags:String
    var BlogBody:String
    var BlogUserID:String
    var BlogWriterName:String
    var BlogDateAndTime:String
    var BlogImageURL:String
    var BlogUserProfileUrl:String
    var BlogDocumentID:String


    constructor(blogTitle:String,blogTags:String,blogBody:String,blogUserID:String,blogWriterName:String,blogDateAndTime:String,blogImageURL:String,blogUserProfileUrl: String,blogDocumentID:String)
    {
        this.BlogTitle=blogTitle
        this.BlogTags=blogTags
        this.BlogBody=blogBody
        this.BlogUserID=blogUserID
        this.BlogWriterName=blogWriterName
        this.BlogDateAndTime=blogDateAndTime
        this.BlogImageURL=blogImageURL
        this.BlogUserProfileUrl=blogUserProfileUrl
        this.BlogDocumentID=blogDocumentID

    }

}