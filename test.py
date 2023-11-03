import boto3
import s3fs
import os
import time
import botocore.config

boto3.set_stream_logger('')

# Establish an EMR client to pass the step to
config = botocore.config.Config(region_name='us-east-1')
emr_client = boto3.client('emr', config=config)

 
