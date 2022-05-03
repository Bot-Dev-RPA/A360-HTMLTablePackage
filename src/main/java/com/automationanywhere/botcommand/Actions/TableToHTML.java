package com.automationanywhere.botcommand.Actions;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;

import static com.automationanywhere.commandsdk.model.DataType.*;

@BotCommand
@CommandPkg(label = "Table to HTML Table",description = "Generate html table from data table",icon = "html.svg", name = "TableToHTML",
		node_label = "from {{inputTable}} and assign to {{returnTo}}",return_description = "Returns HTML string of table element",
		return_required = true,
		return_type = STRING)

public class TableToHTML {

	@Execute
	public StringValue action(
			@Idx(index = "1", type = AttributeType.TABLE)
			@Pkg(label = "Table to generate HTML")
			@NotEmpty
					Table inputTable,
			@Idx(index = "2", type = AttributeType.CHECKBOX)
			@Pkg(label = "enable inline styling",default_value_type = BOOLEAN,default_value = "true")
			@NotEmpty
					Boolean applyStyle,
			@Idx(index = "2.1", type = AttributeType.TEXT)
			@Pkg(label = "Enter table style",default_value_type = STRING,default_value = "border-collapse:collapse;border-color:#ccc;")
					String tableStyle,
			@Idx(index = "2.2", type = AttributeType.TEXT)
			@Pkg(label = "Enter table header style",default_value_type = STRING,default_value = "background-color:#f0f0f0;border-color:#ccc;border-style:solid;border-width:1px;color:#333;\n" +
					"  font-family:Arial, sans-serif;font-size:14px;font-weight:normal;overflow:hidden;padding:10px 5px;word-break:normal;text-align:left;vertical-align:top")
					String headerStyle,
			@Idx(index = "2.3", type = AttributeType.TEXT)
			@Pkg(label = "Enter table row style",default_value_type = STRING)
					String rowStyle,
			@Idx(index = "2.4", type = AttributeType.TEXT)
			@Pkg(label = "Enter table data style",default_value_type = STRING,default_value = "background-color:#fff;border-color:#ccc;border-style:solid;border-width:1px;color:#333;\n" +
					"  font-family:Arial, sans-serif;font-size:14px;overflow:hidden;padding:10px 5px;word-break:normal;text-align:left;vertical-align:top")
					String dataStyle
	) {
		try {
			List<Schema> inputTableSchema = inputTable.getSchema();
			StringBuilder html = new StringBuilder();

			if(!applyStyle){
				html.append("<table>");
				if(inputTableSchema.size()>0)
					html.append("<tr>");
				for (Schema schema : inputTableSchema) {
					html.append("<th>").append(StringEscapeUtils.escapeHtml4(schema.getName())).append("</th>");
				}
				if(inputTableSchema.size()>0)
					html.append("</tr>");
				for (Row row : inputTable.getRows()) {
					html.append("<tr>");
					for (Value data : row.getValues())
						html.append("<td>").append(StringEscapeUtils.escapeHtml4(data.toString())).append("</td>");
					html.append("</tr>");
				}
				html.append("</table>");
			}else {
				html.append("<table style ='" ).append(StringEscapeUtils.escapeHtml4(tableStyle)).append("'>");

				if(inputTableSchema.size()>0)
					html.append("<tr style ='" ).append(StringEscapeUtils.escapeHtml4(rowStyle)).append("'>");
				for (Schema schema : inputTableSchema) {
					html.append("<th style ='").append(StringEscapeUtils.escapeHtml4(headerStyle)).append("'>").append(StringEscapeUtils.escapeHtml4(schema.getName())).append("</th>");
				}
				if(inputTableSchema.size()>0)
					html.append("</tr>");

				for (Row row : inputTable.getRows()) {
					html.append("<tr style ='" ).append(rowStyle).append("'>");
					for (Value data : row.getValues())
						html.append("<td style ='").append(StringEscapeUtils.escapeHtml4(dataStyle)).append("'>").append(StringEscapeUtils.escapeHtml4(data.toString())).append("</td>");
					html.append("</tr>");
				}
				html.append("</table>");

			}

			return new StringValue(html.toString());
		} catch (Exception e) {
			throw new BotCommandException("Error Occurred while generating HTML table: " + e);
		}

	}
}
