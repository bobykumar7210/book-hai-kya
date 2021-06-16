package dev.techasyluminfo.bookhaikaya;

public class Book {
    private String bookTitle;
    private String authorName;
    private String bookDesc;
    private String thumnailUrl;
    private String datepublished;
    private String id;
    private  int pageCount;
    private  String printType;
    private String publisher;
    private  String saleability;
    private String buylink;
    private  String  webReaderLink;

    public Book(String bookTitle, String authorName, String bookDesc, String thumnailUrl, String datepublished, int pageCount, String printType, String publisher, String saleability, String buylink, String webReaderLink) {
        this.bookTitle = bookTitle;
        this.authorName = authorName;
        this.bookDesc = bookDesc;
        this.thumnailUrl = thumnailUrl;
        this.datepublished = datepublished;
        this.pageCount = pageCount;
        this.printType = printType;
        this.publisher = publisher;
        this.saleability = saleability;
        this.buylink = buylink;
        this.webReaderLink = webReaderLink;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getPrintType() {
        return printType;
    }

    public void setPrintType(String printType) {
        this.printType = printType;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getSaleability() {
        return saleability;
    }

    public void setSaleability(String saleability) {
        this.saleability = saleability;
    }

    public String getBuylink() {
        return buylink;
    }

    public void setBuylink(String buylink) {
        this.buylink = buylink;
    }

    public String getWebReaderLink() {
        return webReaderLink;
    }

    public void setWebReaderLink(String webReaderLink) {
        this.webReaderLink = webReaderLink;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public Book(){}

    public Book(String bookTitle, String authorName, String bookDesc, String thumnailUrl, String datepublished, String id) {
        this.bookTitle = bookTitle;
        this.authorName = authorName;
        this.bookDesc = bookDesc;
        this.thumnailUrl = thumnailUrl;
        this.datepublished = datepublished;
        this.id=id;
    }

    public Book(String bookTitle, String authorName, String bookDesc, String thumnailUrl,String datepublished) {
        this.bookTitle = bookTitle;
        this.authorName = authorName;
        this.bookDesc = bookDesc;
        this.thumnailUrl = thumnailUrl;
        this.datepublished=datepublished;

    }

    public String getThumnailUrl() {
        return thumnailUrl;
    }

    public void setThumnailUrl(String thumnailUrl) {
        this.thumnailUrl = thumnailUrl;
    }

    public String getBookDesc() {
        return bookDesc;
    }

    public void setBookDesc(String bookDesc) {
        this.bookDesc = bookDesc;
    }

    public Book(String bookTitle, String authorName, String bookDesc) {
        this.bookTitle = bookTitle;
        this.authorName = authorName;
        this.bookDesc = bookDesc;

    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getDatepublished() {
        return datepublished;
    }

    public void setDatepublished(String datepublished) {
        this.datepublished = datepublished;
    }
}
