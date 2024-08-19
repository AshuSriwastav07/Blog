package com.TLC_Developer.DataManager

class DataClass {

    lateinit var BlogTitle:String
    lateinit var BlogTags:String
    lateinit var BlogBody:String
    lateinit var BlogUserID:String
    lateinit var BlogWriterName:String
    lateinit var BlogDateAndTime:String
    lateinit var BlogImageURL:String


    constructor(BlogTitle:String,BlogTags:String,BlogBody:String,BlogUserID:String,BlogWriterName:String,BlogDateAndTime:String,BlogImageURL:String)
    {
        this.BlogTitle=BlogTitle
        this.BlogTags=BlogTags
        this.BlogBody=BlogBody
        this.BlogUserID=BlogUserID
        this.BlogWriterName=BlogWriterName
        this.BlogDateAndTime=BlogDateAndTime
        this.BlogImageURL=BlogImageURL

    }

}