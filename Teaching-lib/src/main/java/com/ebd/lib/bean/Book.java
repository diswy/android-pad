package com.ebd.lib.bean;

import java.util.List;

public class Book {

    /**
     * Ebook : {"content":"","index":{"content":"","chapters":{"content":"","chapter":[{"content":"","name":"第一章 相交线与平行线","sections":{"content":"","section":[{"content":"","items":{"content":"","item":[{"content":"1.1.1 相交线","page":"12"},{"content":"1.1.2 垂线","page":"13"},{"content":"1.1.3 同位角、内错角、同旁内角","page":"14"}]},"name":"1.1 相交线","page":"9"},{"content":"","items":{"content":"","item":[{"content":"1.2.1 平行线","page":"20"},{"content":"1.2.2 平行线及其判定","page":"21"}]},"name":"1.2 平行线及其判定","page":"19"},{"content":"","items":{"content":"","item":[{"content":"1.3.1 平行线的性质","page":"26"},{"content":"1.3.2 命题、定理、证明","page":"27"}]},"name":"1.3 平行线的性质","page":"25"},{"content":"","name":"1.4 平移","page":"30"}]},"page":"1"},{"content":"","name":"第二章 实数","sections":{"content":"","section":[{"content":"","name":"2.1 平方根","page":"49"},{"content":"","name":"2.2 立方根","page":"41"},{"content":"","name":"2.3 实数","page":"42"}]},"page":"38"}],"alias":"章节"}},"info":{"content":"","name":"七年级语文（上册）","cover":"cover.jpg","totalPage":"300","publish":"人教版","createTime":"2018-9-1","version":"2010版"},"attchments":{"content":"","attchment":[{"content":"前言","page":"1"},{"content":"附录1","page":"500"}]}}
     */

    private EbookBean Ebook;

    public EbookBean getEbook() {
        return Ebook;
    }

    public void setEbook(EbookBean Ebook) {
        this.Ebook = Ebook;
    }

    public static class EbookBean {
        /**
         * content :
         * index : {"content":"","chapters":{"content":"","chapter":[{"content":"","name":"第一章 相交线与平行线","sections":{"content":"","section":[{"content":"","items":{"content":"","item":[{"content":"1.1.1 相交线","page":"12"},{"content":"1.1.2 垂线","page":"13"},{"content":"1.1.3 同位角、内错角、同旁内角","page":"14"}]},"name":"1.1 相交线","page":"9"},{"content":"","items":{"content":"","item":[{"content":"1.2.1 平行线","page":"20"},{"content":"1.2.2 平行线及其判定","page":"21"}]},"name":"1.2 平行线及其判定","page":"19"},{"content":"","items":{"content":"","item":[{"content":"1.3.1 平行线的性质","page":"26"},{"content":"1.3.2 命题、定理、证明","page":"27"}]},"name":"1.3 平行线的性质","page":"25"},{"content":"","name":"1.4 平移","page":"30"}]},"page":"1"},{"content":"","name":"第二章 实数","sections":{"content":"","section":[{"content":"","name":"2.1 平方根","page":"49"},{"content":"","name":"2.2 立方根","page":"41"},{"content":"","name":"2.3 实数","page":"42"}]},"page":"38"}],"alias":"章节"}}
         * info : {"content":"","name":"七年级语文（上册）","cover":"cover.jpg","totalPage":"300","publish":"人教版","createTime":"2018-9-1","version":"2010版"}
         * attchments : {"content":"","attchment":[{"content":"前言","page":"1"},{"content":"附录1","page":"500"}]}
         */

        private String content;
        private IndexBean index;
        private InfoBean info;
        private AttchmentsBean attchments;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public IndexBean getIndex() {
            return index;
        }

        public void setIndex(IndexBean index) {
            this.index = index;
        }

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        public AttchmentsBean getAttchments() {
            return attchments;
        }

        public void setAttchments(AttchmentsBean attchments) {
            this.attchments = attchments;
        }

        public static class IndexBean {
            /**
             * content :
             * chapters : {"content":"","chapter":[{"content":"","name":"第一章 相交线与平行线","sections":{"content":"","section":[{"content":"","items":{"content":"","item":[{"content":"1.1.1 相交线","page":"12"},{"content":"1.1.2 垂线","page":"13"},{"content":"1.1.3 同位角、内错角、同旁内角","page":"14"}]},"name":"1.1 相交线","page":"9"},{"content":"","items":{"content":"","item":[{"content":"1.2.1 平行线","page":"20"},{"content":"1.2.2 平行线及其判定","page":"21"}]},"name":"1.2 平行线及其判定","page":"19"},{"content":"","items":{"content":"","item":[{"content":"1.3.1 平行线的性质","page":"26"},{"content":"1.3.2 命题、定理、证明","page":"27"}]},"name":"1.3 平行线的性质","page":"25"},{"content":"","name":"1.4 平移","page":"30"}]},"page":"1"},{"content":"","name":"第二章 实数","sections":{"content":"","section":[{"content":"","name":"2.1 平方根","page":"49"},{"content":"","name":"2.2 立方根","page":"41"},{"content":"","name":"2.3 实数","page":"42"}]},"page":"38"}],"alias":"章节"}
             */

            private String content;
            private ChaptersBean chapters;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public ChaptersBean getChapters() {
                return chapters;
            }

            public void setChapters(ChaptersBean chapters) {
                this.chapters = chapters;
            }

            public static class ChaptersBean {
                /**
                 * content :
                 * chapter : [{"content":"","name":"第一章 相交线与平行线","sections":{"content":"","section":[{"content":"","items":{"content":"","item":[{"content":"1.1.1 相交线","page":"12"},{"content":"1.1.2 垂线","page":"13"},{"content":"1.1.3 同位角、内错角、同旁内角","page":"14"}]},"name":"1.1 相交线","page":"9"},{"content":"","items":{"content":"","item":[{"content":"1.2.1 平行线","page":"20"},{"content":"1.2.2 平行线及其判定","page":"21"}]},"name":"1.2 平行线及其判定","page":"19"},{"content":"","items":{"content":"","item":[{"content":"1.3.1 平行线的性质","page":"26"},{"content":"1.3.2 命题、定理、证明","page":"27"}]},"name":"1.3 平行线的性质","page":"25"},{"content":"","name":"1.4 平移","page":"30"}]},"page":"1"},{"content":"","name":"第二章 实数","sections":{"content":"","section":[{"content":"","name":"2.1 平方根","page":"49"},{"content":"","name":"2.2 立方根","page":"41"},{"content":"","name":"2.3 实数","page":"42"}]},"page":"38"}]
                 * alias : 章节
                 */

                private String content;
                private String alias;
                private List<ChapterBean> chapter;

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public String getAlias() {
                    return alias;
                }

                public void setAlias(String alias) {
                    this.alias = alias;
                }

                public List<ChapterBean> getChapter() {
                    return chapter;
                }

                public void setChapter(List<ChapterBean> chapter) {
                    this.chapter = chapter;
                }

                public static class ChapterBean {
                    /**
                     * content :
                     * name : 第一章 相交线与平行线
                     * sections : {"content":"","section":[{"content":"","items":{"content":"","item":[{"content":"1.1.1 相交线","page":"12"},{"content":"1.1.2 垂线","page":"13"},{"content":"1.1.3 同位角、内错角、同旁内角","page":"14"}]},"name":"1.1 相交线","page":"9"},{"content":"","items":{"content":"","item":[{"content":"1.2.1 平行线","page":"20"},{"content":"1.2.2 平行线及其判定","page":"21"}]},"name":"1.2 平行线及其判定","page":"19"},{"content":"","items":{"content":"","item":[{"content":"1.3.1 平行线的性质","page":"26"},{"content":"1.3.2 命题、定理、证明","page":"27"}]},"name":"1.3 平行线的性质","page":"25"},{"content":"","name":"1.4 平移","page":"30"}]}
                     * page : 1
                     */

                    private String content;
                    private String name;
                    private SectionsBean sections;
                    private String page;

                    public String getContent() {
                        return content;
                    }

                    public void setContent(String content) {
                        this.content = content;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public SectionsBean getSections() {
                        return sections;
                    }

                    public void setSections(SectionsBean sections) {
                        this.sections = sections;
                    }

                    public String getPage() {
                        return page;
                    }

                    public void setPage(String page) {
                        this.page = page;
                    }

                    public static class SectionsBean {
                        /**
                         * content :
                         * section : [{"content":"","items":{"content":"","item":[{"content":"1.1.1 相交线","page":"12"},{"content":"1.1.2 垂线","page":"13"},{"content":"1.1.3 同位角、内错角、同旁内角","page":"14"}]},"name":"1.1 相交线","page":"9"},{"content":"","items":{"content":"","item":[{"content":"1.2.1 平行线","page":"20"},{"content":"1.2.2 平行线及其判定","page":"21"}]},"name":"1.2 平行线及其判定","page":"19"},{"content":"","items":{"content":"","item":[{"content":"1.3.1 平行线的性质","page":"26"},{"content":"1.3.2 命题、定理、证明","page":"27"}]},"name":"1.3 平行线的性质","page":"25"},{"content":"","name":"1.4 平移","page":"30"}]
                         */

                        private String content;
                        private List<SectionBean> section;

                        public String getContent() {
                            return content;
                        }

                        public void setContent(String content) {
                            this.content = content;
                        }

                        public List<SectionBean> getSection() {
                            return section;
                        }

                        public void setSection(List<SectionBean> section) {
                            this.section = section;
                        }

                        public static class SectionBean {
                            /**
                             * content :
                             * items : {"content":"","item":[{"content":"1.1.1 相交线","page":"12"},{"content":"1.1.2 垂线","page":"13"},{"content":"1.1.3 同位角、内错角、同旁内角","page":"14"}]}
                             * name : 1.1 相交线
                             * page : 9
                             */

                            private String content;
                            private ItemsBean items;
                            private String name;
                            private String page;

                            public String getContent() {
                                return content;
                            }

                            public void setContent(String content) {
                                this.content = content;
                            }

                            public ItemsBean getItems() {
                                return items;
                            }

                            public void setItems(ItemsBean items) {
                                this.items = items;
                            }

                            public String getName() {
                                return name;
                            }

                            public void setName(String name) {
                                this.name = name;
                            }

                            public String getPage() {
                                return page;
                            }

                            public void setPage(String page) {
                                this.page = page;
                            }

                            public static class ItemsBean {
                                /**
                                 * content :
                                 * item : [{"content":"1.1.1 相交线","page":"12"},{"content":"1.1.2 垂线","page":"13"},{"content":"1.1.3 同位角、内错角、同旁内角","page":"14"}]
                                 */

                                private String content;
                                private List<ItemBean> item;

                                public String getContent() {
                                    return content;
                                }

                                public void setContent(String content) {
                                    this.content = content;
                                }

                                public List<ItemBean> getItem() {
                                    return item;
                                }

                                public void setItem(List<ItemBean> item) {
                                    this.item = item;
                                }

                                public static class ItemBean {
                                    /**
                                     * content : 1.1.1 相交线
                                     * page : 12
                                     */

                                    private String content;
                                    private String page;

                                    public String getContent() {
                                        return content;
                                    }

                                    public void setContent(String content) {
                                        this.content = content;
                                    }

                                    public String getPage() {
                                        return page;
                                    }

                                    public void setPage(String page) {
                                        this.page = page;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        public static class InfoBean {
            /**
             * content :
             * name : 七年级语文（上册）
             * cover : cover.jpg
             * totalPage : 300
             * publish : 人教版
             * createTime : 2018-9-1
             * version : 2010版
             */

            private String content;
            private String name;
            private String cover;
            private String totalPage;
            private String publish;
            private String createTime;
            private String version;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public String getTotalPage() {
                return totalPage;
            }

            public void setTotalPage(String totalPage) {
                this.totalPage = totalPage;
            }

            public String getPublish() {
                return publish;
            }

            public void setPublish(String publish) {
                this.publish = publish;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }
        }

        public static class AttchmentsBean {
            /**
             * content :
             * attchment : [{"content":"前言","page":"1"},{"content":"附录1","page":"500"}]
             */

            private String content;
            private List<AttchmentBean> attchment;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public List<AttchmentBean> getAttchment() {
                return attchment;
            }

            public void setAttchment(List<AttchmentBean> attchment) {
                this.attchment = attchment;
            }

            public static class AttchmentBean {
                /**
                 * content : 前言
                 * page : 1
                 */

                private String content;
                private String page;

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public String getPage() {
                    return page;
                }

                public void setPage(String page) {
                    this.page = page;
                }
            }
        }
    }
}
