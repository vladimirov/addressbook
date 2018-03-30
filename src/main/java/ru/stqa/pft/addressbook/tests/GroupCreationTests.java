package ru.stqa.pft.addressbook.tests;


import com.thoughtworks.xstream.XStream;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.stqa.pft.addressbook.model.GroupData;
import ru.stqa.pft.addressbook.model.Groups;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class GroupCreationTests extends TestBase {

    @DataProvider
    public Iterator<Object[]> validGroups() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File("src/main/resources/groups.xml")));
        String xml = "";
        String line = reader.readLine();
        while (line != null) {
//            String[] split = line.split(";");
//            list.add(new Object[]{new GroupData().withName(split[0]).withHeader(split[1]).withFooter(split[2])});
            xml += line;
            line = reader.readLine();
        }
        XStream xStream = new XStream();
        xStream.processAnnotations(GroupData.class);
        List<GroupData> groups = (List<GroupData>) xStream.fromXML(xml);
        return groups.stream().map((g) -> new Object[] {g}).collect(Collectors.toList()).iterator();
    }

    @Test(dataProvider = "validGroups")
    public void testGroupCreation(GroupData group) {
        app.goTo().groupPage();
        Groups before = app.group().all();
        app.group().create(group);
        assertThat(app.group().count(), equalTo(before.size() + 1));//hashing and pre-validation
        Groups after = app.group().all();
        assertThat(after, equalTo(
                before.withAdded(group.withId(after.stream().mapToInt((g) -> g.getId()).max().getAsInt()))));
    }

    @Test(enabled = false)
    public void testNegativeGroupCreation() {
        app.goTo().groupPage();
        Groups before = app.group().all();
        GroupData group = new GroupData().withName("test'");
        app.group().create(group);
        assertThat(app.group().count(), equalTo(before.size()));//hashing and pre-validation
        Groups after = app.group().all();
        assertThat(after, equalTo(before));
    }

}


//Вычисление максимального значения 'value' у группы
//        int max = 0;
//        for (GroupData g : after) {
//            if (g.getId() > max) {
//                max = g.getId();
//            }
//        }

//Вычисление максимального значения 'value' у группы с помощью люмбда выражений
//int max = after.stream().max(Comparator.comparingInt(GroupData::getId)).get().getId();
//group.withId(max);
//before.add(group);

//Для сравненя множеств
//Comparator<? super GroupData> byId = Comparator.comparingInt(GroupData::getId);
//before.sort(byId);
//after.sort(byId);

