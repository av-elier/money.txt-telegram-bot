package ru.av_elier.money.txt;

import net.objecthunter.exp4j.ExpressionBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Adelier on 16.10.2015.
 */
public class MoneyTxtLine {
    private static final Pattern re = Pattern.compile(" *(\\d\\d\\d\\d\\.\\d\\d\\.\\d\\d) *(.*?) +([\\(\\)\\d\\+-\\.,\\*/]+)\\w*(.*)");
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");

    private Optional<Date> date;
    private String category;
    private String expenseExpr;
    private Double expenseAmount; // BigDecimal is more precise, but...
    private String name;
    private Set<String> hashtags;

    public MoneyTxtLine(String txtLine) throws ParseException {
        Matcher m = re.matcher(txtLine);
        if (!m.matches())
            throw new ParseException("Pattern not matches", -1);
        String sDate = m.group(1);
        Date date = formatter.parse(sDate);
        this.setDate(Optional.of(date));
        this.setCategory(m.group(2));
        this.setExpenseExpr(m.group(3));
        this.setName( m.group(4) );
        this.setHashtags(new HashSet<>());
    }

    public MoneyTxtLine(Optional<Date> date, String category, String expenseExpr, String name, Set<String> hashtags) {
        this.date = date;
        this.category = category;
        this.expenseExpr = expenseExpr;
        this.name = name;
        this.hashtags = hashtags;
    }

    public Optional<Date> getDate() {
        return date;
    }

    public void setDate(Optional<Date> date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExpenseExpr() {
        return expenseExpr;
    }

    public void setExpenseExpr(String expenseExpr) {
        this.expenseExpr = expenseExpr;
        this.expenseAmount =  new ExpressionBuilder(expenseExpr).build().evaluate();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(Set<String> hashtags) {
        this.hashtags = hashtags;
    }

    public Double getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(Double expenseAmount) {
        this.expenseAmount = expenseAmount;
        this.expenseExpr = expenseAmount.toString();
    }

    @Override
    public String toString() {
        String sHashtags;
        if (hashtags == null || hashtags.isEmpty())
            sHashtags = "";
        else
            sHashtags = hashtags.stream().collect(Collectors.joining(" #", "#", ""));
        return
                (formatter.format( date.orElse(Date.from(Instant.now())) ) +
                " " + category +
                " " + expenseExpr +
                " " + name +
                " " + sHashtags).trim();
    }
}
