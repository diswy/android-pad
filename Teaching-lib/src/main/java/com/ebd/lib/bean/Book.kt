package com.ebd.lib.bean



data class Book(
    val Ebook: Ebook
)

data class Ebook(
    val content: String,
    val chapters: Chapters,
    val info: Info,
    val indexs: Indexs
)

data class Chapters(
    val content: String,
    val chapter: List<Chapter>?,
    val alias: String
)

data class Chapter(
    val content: String,
    val name: String,
    val sections: Sections,
    val page: String
)

data class Sections(
    val content: String,
    val section: List<Section>?
)

data class Section(
    val content: String,
    val items: Items?,
    val name: String,
    val page: String
)

data class Items(
    val content: String,
    val item: List<Item>
)

data class Item(
    val content: String,
    val page: String
)

data class Indexs(
    val content: String,
    val name: String,
    val index: List<Index>
)

data class Index(
    val content: String,
    val page: String
)

data class Info(
    val content: String,
    val name: String,
    val cover: String,
    val proofreader: String,
    val totalPage: String,
    val publish: String,
    val createTime: String,
    val version: String
)