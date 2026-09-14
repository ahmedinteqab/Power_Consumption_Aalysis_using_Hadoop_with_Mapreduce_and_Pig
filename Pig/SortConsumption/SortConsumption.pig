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

clean_data = FILTER data BY Global_active_power IS NOT NULL;

sorted_data = ORDER clean_data BY Global_active_power DESC;

STORE sorted_data
INTO 'file:///D:/PowerConsumption/Pig/output/SortConsumption'
USING PigStorage('\t');