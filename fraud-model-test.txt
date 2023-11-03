import boto3
import time
import botocore.config

def check_connection():
  """Checks that the script can connect to the EMR cluster."""
  print("Checking connection to Cluster...")
  emr_client = boto3.client('emr', config=botocore.config.Config(region_name='us-east-1'))
  cluster_id = 'j-2856DP5PDB1KR'
  try:
    emr_client.describe_cluster(ClusterId=cluster_id)
    return True
  except Exception:
    return False

def run_preprocessing(job_name, jar_file, input_data_path, output_data_path):
  """Runs the preprocessing.jar script on the EMR cluster."""
  jar_zipfile = "s3://ai-txg-graph-2023/code/jar.zip"
  args = [jar_zipfile, "inputs", "outputs"]
  client = boto3.client("emr")
  client.run_job_flow(
    Name=job_name,

    JobFlowRole="AWSRoleForEC2EMR",
    ServiceRole="EMR_DefaultRole",

    Instances={
    "InstanceCount": 3,
    "MasterInstanceType": "c3.xlarge",
    "SlaveInstanceType": "c3.xlarge"
    },
    Steps=[
    {
      "Name": "Run Preprocessing",
      "ActionOnFailure": "CONTINUE",
      "HadoopJarStep": {
      "Jar": jar_zipfile,
      "Args": args
      }
    }
    ],
    ReleaseLabel="emr-5.36.0"
  )

def train_model(neo4j_client, cypher_command):
  loading_complete = False
  while not loading_complete:
    time.sleep(10)
    loading_complete = emr_client.describe_job_flows(JobFlowIds=[job_name])['JobFlows'][0]['Status']['State'] == 'WAITING'

  neo4j_client.run_cypher(cypher_command, database='fraud_detection')

def dump_model(neo4j_client, model_path, model_name):
  neo4j_client.dump_model(model_path, model=model_name, database='fraud_detection')

def upload_model(s3_client, model_path, bucket_name, model_name):
  s3_client.upload_file(model_path, 'my-bucket', 'model.json')

def dump_model_output(neo4j_client, model_output_path, model_name):
  neo4j_client.dump_model_output(model_output_path, model=model_name, database='fraud_detection')

def upload_model_output(s3_client, model_output_path, bucket_name, model_output_name):
  s3_client.upload_file(model_output_path, 'my-bucket', 'model_output.json')

def main():
  job_name = 'preprocessing-job'
  jar_file = 'preprocessing.jar'
  input_data_path = 's3://my-bucket/data/input'
  output_data_path = 's3://my-bucket/data/output'

if check_connection():
  print("Connected to EMR cluster. Running preprocessing")
  run_preprocessing(job_name, jar_file, input_data_path, output_data_path)
else:
  print("Could not connect to EMR cluster.")
  exit(1)

  neo4j_endpoint_url = '10.194.29.220:7474'

  neo4j_client = boto3.client('neo4j', endpoint_url=neo4j_endpoint_url)

  # run_emr_job(job_name, jar_file, input_data_path, output_data_path)

  """ TRAIN MODEL """
  cypher_command = """
  CREATE (model:Model {name: 'fraud_model'})
  CALL apoc.linearRegression.fit(
    'transaction_amount',
    'is_fraudulent',
    'data',
    model
  )
  """
  train_model(neo4j_client, cypher_command)

  """ TRANSFER OUTPUTS TO S3 """
  dump_model(neo4j_client, model_path, model_name)
  upload_model(s3_client, model_path)
  dump_model_output(neo4j_client, model_output_path, model_name)
  upload_model_output(s3_client, model_output_path)

if __name__ == '__main__':
  main()
