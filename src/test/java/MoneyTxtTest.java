import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.av_elier.money.txt.LocalTxtCommunicator;
import ru.av_elier.money.txt.MoneyTxtLine;
import ru.av_elier.money.txt.TxtCommunicator;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Adelier on 16.10.2015.
 */
public class MoneyTxtTest {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    @Test
    public void testMoneyLineCreation() throws ParseException {
        String[] testLines = {"2015.09.15 food, bread 13",
                "2015.09.15 food, sugar 55",
                "2015.09.15 food, milky 49 milk",
                "2015.09.25 food,sweets 54+36+10.3*5+14.3*5 something",
                "2015.09.25 food;not at home 37",
                "2015.09.26 food; not at home 35+50+59+79+45+54+79",
                "2015.09.26 misc +520 grandparents",
                "2015.09.26 renovation 43 paint",
                "2015.09.26 renovation 127.5 уголки",
                "2015.09.26 household items 170 bathroom basket",
                "2015.09.26 household items 21 soap dish",
                "2015.09.26 transport 17*4",
                "2015.09.26 transport 17",
                "2015.09.27 food, bread 13",
                "2015.09.27 food, sugar 50",
                "2015.09.27 food, milky 49 milk",
                "2015.10.01 food, bread 13",
                "2015.10.01 food, sugar 50",
                "2015.10.01 food, milky 49 milk"};
        for (String testLine : testLines) {
            MoneyTxtLine line = new MoneyTxtLine(testLine);
            System.out.printf("%s CATEGORY: %s AMOUNT: %f NAME: %s\n",
                    line.getDate().toString(), line.getCategory(), line.getExpenseAmount(), line.getName());
        }
    }

    @Test
    public void testLocalTxtCommunicator() throws IOException, ParseException {
        File moneyTxt = folder.newFile("money.txt");
        TxtCommunicator txtComm = new LocalTxtCommunicator(moneyTxt);
        MoneyTxtLine moneyTxtLine = new MoneyTxtLine("2015.09.15 food, sugar 55");
        Assert.assertEquals("2015.09.15 food, sugar 55", moneyTxtLine.toString().trim());

        System.out.println("Adding line: " + moneyTxtLine);
        txtComm.addLine(moneyTxtLine);
        System.out.println("All content: " + FileUtils.readFileToString(moneyTxt));
        Assert.assertEquals("2015.09.15 food, sugar 55", FileUtils.readFileToString(moneyTxt).trim());

        System.out.println("Last line:   " + txtComm.getLastLine().get() );
        Assert.assertEquals("2015.09.15 food, sugar 55", txtComm.getLastLine().get().toString().trim());

        MoneyTxtLine anotherMoneyTxtLine = new MoneyTxtLine("2015.10.01 food, bread 13");
        System.out.println("Adding line: " + anotherMoneyTxtLine);
        txtComm.addLine(anotherMoneyTxtLine);

        List<MoneyTxtLine> twoLines = txtComm.getLastLines(5);
        System.out.println("twoLines: " + twoLines);
                Assert.assertEquals(2, twoLines.size());

        List<MoneyTxtLine> oneLine = txtComm.getLastLines(1);
        System.out.println("oneLine:  " + oneLine);
                Assert.assertEquals(1, oneLine.size());
    }

}
