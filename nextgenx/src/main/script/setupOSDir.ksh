#/usr/bin/ksh
#Argument to the script is a base folder location
if [ "$1" = "" ];
then
	echo "Please pass the base folder location"
	exit 1
fi
base=${1:-/avaloq/PERDB}
if [ ! -r $base -o ! -w $base -o ! -x $base -a ! -d $base ];
then
        echo "Error please check ${base} exists with access permissions"
        exit 1
fi

#Create the folder set
mkdir $base/external
mkdir $base/external/incoming
mkdir $base/external/incoming/bsb
mkdir $base/external/incoming/bsb/archive
mkdir $base/external/incoming/bsb/ngperdbldr_out
mkdir $base/external/incoming/bpay_biller
mkdir $base/external/incoming/bpay_biller/archive
mkdir $base/external/incoming/bpay_biller/ngperdbldr_out
#If loader user and Oracle process user are not in the same Unix group 
#the following is needed
#chmod 777 $base
#Assume directory permissions of 2755 for $base
#chmod -R 777 $base/external
if [ $? != 0 ]
then
	echo "Error in folders setup for incoming files"
	exit 1
fi
exit 0
