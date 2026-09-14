data = LOAD 'file:///D:/PowerConsumption/data/household_power_consumption.csv'
       USING PigStorage(',')
       AS (
           Date:chararray,
           Time:chararray,
           Global_active_power:double,
           Global_reactive_power:double,
           Voltage:double,
           Global_intensity:double,
           Sub_metering_1:double,
           Sub_metering_2:double,
           Sub_metering_3:double
       );

clean_data = FILTER data BY Voltage IS NOT NULL;

grouped_data = GROUP clean_data ALL;

maximum_voltage = FOREACH grouped_data GENERATE
                  MAX(clean_data.Voltage) AS Maximum_Voltage;

STORE maximum_voltage
INTO 'file:///D:/PowerConsumption/Pig/output/MaximumVoltage'
USING PigStorage('\t');