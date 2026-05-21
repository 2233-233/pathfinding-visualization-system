import subprocess
import os

repo = r'F:/javadevelope/bishe-workplace-github'
git_dir = repo + '/.git'

files_to_delete = [
    '毕业设计进度报告.md',
    '第2章系统分析.txt',
    '第3章系统设计.txt',
    '第5章系统测试.txt',
    '设计目标板块.txt'
]

# First, physically delete files
for f in files_to_delete:
    filepath = os.path.join(repo, f)
    if os.path.exists(filepath):
        os.remove(filepath)
        print(f'Deleted: {f}')
    else:
        print(f'Already missing: {f}')

# Then git add -A to detect deletions, then commit and push
result = subprocess.run(
    ['git', '--git-dir='+git_dir, '--work-tree='+repo, 'add', '-A'],
    capture_output=True, text=True, shell=True
)
print(f'git add: {result.returncode}')

result = subprocess.run(
    ['git', '--git-dir='+git_dir, '--work-tree='+repo, 'commit', '-m', '删除不需要的文档文件'],
    capture_output=True, text=True, shell=True
)
print(f'git commit: {result.returncode}')
print(result.stdout)
print(result.stderr)

result = subprocess.run(
    ['git', '--git-dir='+git_dir, '--work-tree='+repo, 'push'],
    capture_output=True, text=True, shell=True
)
print(f'git push: {result.returncode}')
print(result.stdout)
print(result.stderr)
