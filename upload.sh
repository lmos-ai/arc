#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
#
# SPDX-License-Identifier: Apache-2.0

set -eo xtrace

FILE=$1
TOKEN=$2

curl -X 'POST' \
'https://central.sonatype.com/api/v1/publisher/upload?publishingType=AUTOMATIC' \
-H 'accept: text/plain' \
-H "Authorization: Bearer ${TOKEN}" \
-H 'Content-Type: multipart/form-data' \
-F "bundle=@${FILE};type=application/zip"
