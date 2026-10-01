"""在 build 下的隔离副本中加入显式启用的回归探针，不修改整合包原始文件。"""
from pathlib import Path
import shutil
import zipfile

root = Path(__file__).resolve().parents[1]
source = root / 'build/libs/ageofexpansionmodpackfix-1.0.0.jar'
server = root / 'build/chunkProbeServer/mods/ageofexpansionmodpackfix-1.0.0.jar'
client = root / 'build/reportedChunkClient/mods/ageofexpansionmodpackfix-1.0.0.jar'
classes = root / 'build/classes/java/gameTest'
names = ('ReportedChunkServerProbe', 'ReportedChunkClientProbe', 'ChunkRenderProbe')
probes = [p for p in classes.rglob('*.class') if p.stem.split('$')[0] in names]
assert {p.stem for p in probes} >= set(names), '先运行 compileGameTestJava'
assert server.parent.is_dir() and client.parent.is_dir(), '先准备隔离测试目录'

with zipfile.ZipFile(source) as original, zipfile.ZipFile(server, 'w', zipfile.ZIP_DEFLATED) as output:
    for entry in original.infolist():
        output.writestr(entry.filename, original.read(entry.filename))
    for probe in probes:
        output.write(probe, probe.relative_to(classes).as_posix())
with zipfile.ZipFile(server) as output:
    assert output.testzip() is None
shutil.copy2(server, client)
print('仅在 build 隔离副本中打包回归探针；原始测试目录未修改。')
