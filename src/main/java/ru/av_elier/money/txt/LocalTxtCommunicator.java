package ru.av_elier.money.txt;

import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Adelier on 16.10.2015.
 */
public class LocalTxtCommunicator extends TxtCommunicator {
    private File moneyTxt;

    public LocalTxtCommunicator() {
        this.moneyTxt = new File("money.txt");
    }
    public LocalTxtCommunicator(File moneyTxt) {
        this.moneyTxt = moneyTxt;
    }

    @Override
    public void addLine(MoneyTxtLine line) {
        try {
            FileUtils.write(moneyTxt, line.toString() + "\n", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<MoneyTxtLine> getLastLines(int limit) {
        CircularFifoBuffer fifoBuffer = new CircularFifoBuffer(limit);
        LineIterator it = null;
        try {
            it = FileUtils.lineIterator(moneyTxt);
            while (it.hasNext()) {
                String line = it.nextLine();
                fifoBuffer.add(line);
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            LineIterator.closeQuietly(it);
        }
        Stream<String> sStrLines = fifoBuffer.stream().map(objLine -> ((String)objLine));
        Stream<MoneyTxtLine> sMoney = sStrLines.map(line -> { try {
                return new MoneyTxtLine(line);
            } catch (ParseException e) {
                return null; }}).filter(line -> line != null);
        List<MoneyTxtLine> res = sMoney.collect(Collectors.toList());
        return res;
    }
}
