package com.automationanywhere.botcommand.Actions;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.automationanywhere.commandsdk.model.DataType.LIST;
import static com.automationanywhere.commandsdk.model.DataType.TABLE;

@BotCommand
@CommandPkg(label = "Get Tables",description = "Get list of tables present in HTML",icon = "html.svg", name = "HTMLTable",
		node_label = "from {{HTMLText}} and assign to {{returnTo}}",return_description = "Returns list of table",
		return_required = true,
		return_type = LIST, return_sub_type =TABLE)

public class HTMLTable {

	@Execute
	public ListValue action(
			@Idx(index = "1", type = AttributeType.TEXT)
			@Pkg(label = "Set HTML text value, containing tables with horizontal headers")
			@NotEmpty
					String HTMLText

	) {
		try {
			ListValue retListValue = new ListValue();
			ArrayList<TableValue> returnList = new ArrayList<>();

			Document doc = Jsoup.parse(HTMLText);
			Elements tables = doc.select("table");

			for(Element table : tables) {
				List<Schema> schemaList = new ArrayList<>();
				List<Row> rowList = new ArrayList<>();
				int maxColumnCount = 0;

				Elements rows = table.select("tr");
				if(rows.size()>0){

					if(rows.get(0).select("th").eachText().size()>0)
						rows.get(0).select("th").eachText().forEach(header -> schemaList.add(new Schema(header.strip())));
					else
						rows.get(0).select("td").eachText().forEach(header -> schemaList.add(new Schema(header.strip())));

					maxColumnCount = schemaList.size();

					for(int row=1;row<rows.size();row++) {
						List<Value> rowValue = new ArrayList<>();
						rows.get(row).select("td").eachText().forEach(data -> rowValue.add(new StringValue(data.strip())));

						if(rowValue.size()>maxColumnCount)
							maxColumnCount = rowValue.size();

						rowList.add(new Row(rowValue));
					}

					while(schemaList.size()< maxColumnCount)
						schemaList.add(new Schema(""));
				}



				Table Output = new Table(schemaList,rowList);
				returnList.add(new TableValue(Output));
			}

			retListValue.set(returnList);
			return retListValue;

		} catch (Exception e) {
			throw new BotCommandException("Error Occurred while finding table in HTML: " + Arrays.toString(e.getStackTrace()));
		}



	}
}
